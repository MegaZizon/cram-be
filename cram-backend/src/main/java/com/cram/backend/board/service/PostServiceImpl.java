package com.cram.backend.board.service;

import com.cram.backend.board.dto.*;
import com.cram.backend.board.entity.*;
import com.cram.backend.board.repository.*;
import com.cram.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final com.cram.backend.user.repository.UserRepository userRepository;
    private final PostFileRepository postFileRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public PostCreateResponse createPost(Long groupId, PostCreateRequest request) {
        log.info("Creating post for group: {}, request: {}", groupId, request);

        // 1. 게시판 카테고리 존재 여부 확인
        BoardCategory boardCategory = boardCategoryRepository.findByIdAndStudyGroup_Id(request.getBoardId(), groupId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. Board ID: " + request.getBoardId() + ", Group ID: " + groupId));

        // 2. 사용자 존재 여부 확인
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. User ID: " + request.getUserId()));

        // 3. Board 엔티티는 별도로 조회하지 않음 (PostCreateRequest에 boardId가 있지만 실제로는 boardCategoryId를 의미)
        // Post 엔티티에서 board 필드가 있다면 null로 설정하거나 별도 처리 필요

        // 4. 게시글 엔티티 생성 및 저장
        Post post = Post.builder()
                .boardCategory(boardCategory)
                .board(null) // Board 엔티티가 따로 없으므로 null 또는 생략
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savedPost = postRepository.save(post);

        // 5. 첨부파일 처리
        List<PostCreateResponse.AttachedFileInfo> attachedFiles = new ArrayList<>();
        if (request.getAttachmentList() != null) {
            for (PostCreateRequest.AttachmentInfo attachment : request.getAttachmentList()) {
                PostAttachment postAttachment = PostAttachment.builder()
                        .post(savedPost)
                        .originalName(attachment.getOriginalFileName())
                        .filePath(attachment.getSavedFilePath())
                        .savedName(attachment.getUuidFileName())
                        .build();
                PostAttachment savedFile = postFileRepository.save(postAttachment);

                attachedFiles.add(PostCreateResponse.AttachedFileInfo.builder()
                        .fileId(savedFile.getId())
                        .filePath(savedFile.getFilePath())
                        .originalFileName(savedFile.getOriginalName())
                        .uuidFileName(savedFile.getSavedName())
                        .build());
            }
        }

        // 6. 응답 DTO 생성
        return PostCreateResponse.builder()
                .id(savedPost.getId())
                .categoryId(savedPost.getBoardCategory().getId())
                .boardId(savedPost.getBoard() != null ? savedPost.getBoard().getId() : savedPost.getBoardCategory().getId())
                .userId(savedPost.getUser().getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .createdAt(savedPost.getCreatedAt())
                .isDeleted(savedPost.getIsDeleted())
                .viewCount(savedPost.getViewCount())
                .attachedFiles(attachedFiles)
                .build();
    }

    @Override
    @Transactional
    public PostDetailResponse getPostDetail(Long groupId, Long postId) {
        log.info("Getting post detail: {} for group: {}", postId, groupId);

        // 1. 게시글 존재 여부 확인 및 조회
        Post post = postRepository.findByIdAndBoardCategory_StudyGroup_Id(postId, groupId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. Post ID: " + postId + ", Group ID: " + groupId));

        // 2. 조회수 증가
        postRepository.incrementViewCount(postId);

        // 3. 현재 사용자의 좋아요 여부 확인 (임시로 false)
        boolean isLiked = false;

        // 4. 좋아요 수와 댓글 수 계산
        int likeCount = (int) likeRepository.countByPost_Id(postId);
        int commentCount = 0; // 댓글 Repository가 있다면 구현

        // 5. 응답 DTO 생성
        PostDetailResponse.BoardInfo boardInfo = PostDetailResponse.BoardInfo.builder()
                .id(post.getBoardCategory().getId())
                .name(post.getBoardCategory().getName())
                .type(post.getBoardCategory().getBoardType().getDescription())
                .build();

        PostDetailResponse.AuthorInfo authorInfo = PostDetailResponse.AuthorInfo.builder()
                .userId(post.getUser().getId())
                .username(post.getUser().getName())
                .profileImage(post.getUser().getProfileImage())
                .build();

        PostDetailResponse.PostDetailData data = PostDetailResponse.PostDetailData.builder()
                .board(boardInfo)
                .author(authorInfo)
                .postId(post.getId())
                .title(post.getTitle())
                .boardName(post.getBoardCategory().getName())
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .viewCount(post.getViewCount())
                .isLiked(isLiked)
                .build();

        return PostDetailResponse.builder()
                .message("게시글을 조회했습니다.")
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public PostUpdateResponse updatePost(Long groupId, Long postId, PostUpdateRequest request) {
        log.info("Updating post: {} for group: {}, request: {}", postId, groupId, request);

        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findByIdAndBoardCategory_StudyGroup_Id(postId, groupId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. Post ID: " + postId + ", Group ID: " + groupId));

        // 2. 게시글 정보 수정
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        postRepository.save(post);

        // 3. 좋아요 수와 댓글 수 계산
        int likeCount = (int) likeRepository.countByPost_Id(postId);
        int commentCount = 0;
        boolean isLiked = false;

        // 4. 응답 DTO 생성
        PostUpdateResponse.BoardInfo boardInfo = PostUpdateResponse.BoardInfo.builder()
                .id(post.getBoardCategory().getId())
                .name(post.getBoardCategory().getName())
                .type(post.getBoardCategory().getBoardType().getDescription())
                .build();

        PostUpdateResponse.AuthorInfo authorInfo = PostUpdateResponse.AuthorInfo.builder()
                .userId(post.getUser().getId())
                .username(post.getUser().getName())
                .profileImage(post.getUser().getProfileImage())
                .build();

        PostUpdateResponse.PostUpdateData data = PostUpdateResponse.PostUpdateData.builder()
                .board(boardInfo)
                .author(authorInfo)
                .postId(post.getId())
                .title(post.getTitle())
                .boardName(post.getBoardCategory().getName())
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .viewCount(post.getViewCount())
                .isLiked(isLiked)
                .build();

        return PostUpdateResponse.builder()
                .message("게시글을 수정했습니다.")
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public PostDeleteResponse deletePost(Long groupId, Long postId) {
        log.info("Deleting post: {} for group: {}", postId, groupId);

        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findByIdAndBoardCategory_StudyGroup_Id(postId, groupId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. Post ID: " + postId + ", Group ID: " + groupId));

        // 2. 게시글 삭제 (soft delete)
        post.setIsDeleted(true);
        postRepository.save(post);

        // 3. 응답 DTO 생성
        PostDeleteResponse.PostDeleteData data = PostDeleteResponse.PostDeleteData.builder()
                .postId(post.getId())
                .build();

        return PostDeleteResponse.builder()
                .status(200)
                .message("게시글이 삭제되었습니다.")
                .data(data)
                .build();
    }
}