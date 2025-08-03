package com.cram.backend.board.service;

import com.cram.backend.board.dto.*;

public interface PostService {

    /**
     * 특정 게시판에 새로운 게시글 작성
     */
    PostCreateResponse createPost(Long groupId, PostCreateRequest request);

    /**
     * 특정 게시글의 상세 내용 조회
     */
    PostDetailResponse getPostDetail(Long groupId, Long postId);

    /**
     * 특정 게시글의 내용 수정
     */
    PostUpdateResponse updatePost(Long groupId, Long postId, PostUpdateRequest request);

    /**
     * 특정 게시글 삭제
     */
    PostDeleteResponse deletePost(Long groupId, Long postId);
}