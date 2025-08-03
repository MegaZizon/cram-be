package com.cram.backend.board.service;

import com.cram.backend.board.dto.*;
import com.cram.backend.board.entity.BoardCategory;
import com.cram.backend.board.repository.BoardCategoryRepository;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardCategoryRepository boardCategoryRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public BoardCreateResponse createBoard(Long groupId, BoardCreateRequest request) {
        log.info("Creating board for group: {}, request: {}", groupId, request);

        // 1. 스터디 그룹 존재 여부 확인
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("스터디 그룹을 찾을 수 없습니다. ID: " + groupId));

        // 2. 게시판 타입 변환
        BoardCategory.BoardType boardType;
        try {
            boardType = "학습".equals(request.getType()) ?
                    BoardCategory.BoardType.STUDY : BoardCategory.BoardType.COMMUNICATION;
        } catch (Exception e) {
            throw new RuntimeException("잘못된 게시판 타입입니다: " + request.getType());
        }

        // 3. 순서 계산 (가장 마지막 순서 + 1)
        Integer nextOrder = boardCategoryRepository.findMaxOrderByStudyGroupId(groupId) + 1;

        // 4. 게시판 카테고리 엔티티 생성 및 저장
        BoardCategory boardCategory = BoardCategory.builder()
                .studyGroup(studyGroup)
                .boardType(boardType)
                .name(request.getBoardName())
                .description("") // 기본 설명
                .orderIndex(nextOrder)
                .build();

        BoardCategory savedBoardCategory = boardCategoryRepository.save(boardCategory);

        // 5. 응답 DTO 생성
        return BoardCreateResponse.builder()
                .categoryId(savedBoardCategory.getId())
                .categoryName(request.getCategoryName() != null ?
                        request.getCategoryName() : savedBoardCategory.getBoardType().getDescription())
                .type(savedBoardCategory.getBoardType().getDescription())
                .boardId(savedBoardCategory.getId()) // BoardCategory ID를 boardId로 사용
                .boardName(savedBoardCategory.getName())
                .groupId(savedBoardCategory.getStudyGroup().getId())
                .categoryIdInBoard(savedBoardCategory.getId())
                .build();
    }

    @Override
    public StudyGroupBoardListResponse getStudyGroupBoardList(Long groupId) {
        log.info("Getting board list for group: {}", groupId);

        // 1. 스터디 그룹 존재 여부 확인 및 정보 조회
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("스터디 그룹을 찾을 수 없습니다. ID: " + groupId));

        // 2. 해당 그룹의 게시판 목록 조회 (삭제되지 않은 것만)
        List<BoardCategory> boardCategories = boardCategoryRepository
                .findByStudyGroup_IdAndIsDeletedFalseOrderByOrderIndexAsc(groupId);

        // 3. 현재 멤버 수 조회 (임시로 0 설정)
        int currentMemberCount = 0;
        // TODO: GroupMemberRepository가 준비되면 아래 코드 사용
        // int currentMemberCount = (int) groupMemberRepository.countByStudyGroup_IdAndIsActiveTrue(groupId);

        // 4. 응답 DTO 생성
        StudyGroupBoardListResponse.GroupMetaInfo groupMetaInfo = StudyGroupBoardListResponse.GroupMetaInfo.builder()
                .name(studyGroup.getName())
                .currentMembers(currentMemberCount)
                .description(studyGroup.getDescription())
                .thumbnailUrl(studyGroup.getThumbnailUrl())
                .build();

        List<StudyGroupBoardListResponse.CommunityBoardInfo> communityBoards = boardCategories.stream()
                .map(boardCategory -> StudyGroupBoardListResponse.CommunityBoardInfo.builder()
                        .name(boardCategory.getName())
                        .url("https://cram.com/board/" + boardCategory.getId())
                        .build())
                .collect(Collectors.toList());

        StudyGroupBoardListResponse.StudyGroupData data = StudyGroupBoardListResponse.StudyGroupData.builder()
                .groupMetaInfo(groupMetaInfo)
                .communityBoard(communityBoards)
                .build();

        return StudyGroupBoardListResponse.builder()
                .message("스터디 그룹 상세 정보를 조회했습니다.")
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public BoardEditResponse editBoard(Long groupId, Long boardId, BoardEditRequest request) {
        log.info("Editing board: {} for group: {}, request: {}", boardId, groupId, request);

        // 1. 게시판 존재 여부 및 권한 확인
        BoardCategory boardCategory = boardCategoryRepository.findByIdAndStudyGroup_Id(boardId, groupId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. Board ID: " + boardId + ", Group ID: " + groupId));

        // 2. 게시판 정보 수정
        if (request.getName() != null) {
            boardCategory.setName(request.getName());
        }
        if (request.getDescription() != null) {
            boardCategory.setDescription(request.getDescription());
        }

        boardCategoryRepository.save(boardCategory);

        // 3. 응답 DTO 생성
        BoardEditResponse.BoardData data = BoardEditResponse.BoardData.builder()
                .id(boardCategory.getId())
                .type(boardCategory.getBoardType().getDescription())
                .name(boardCategory.getName())
                .description(boardCategory.getDescription())
                .build();

        return BoardEditResponse.builder()
                .message("게시판 이름을 수정했습니다.")
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public BoardOrderUpdateResponse updateBoardOrder(Long groupId, Long boardId, BoardOrderUpdateRequest request) {
        log.info("Updating board order: {} for group: {}, new order: {}", boardId, groupId, request.getNewOrder());

        // 1. 게시판 존재 여부 및 권한 확인
        BoardCategory boardCategory = boardCategoryRepository.findByIdAndStudyGroup_Id(boardId, groupId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. Board ID: " + boardId + ", Group ID: " + groupId));

        // 2. 순서 변경 로직
        Integer currentOrder = boardCategory.getOrderIndex();
        Integer newOrder = request.getNewOrder();

        if (!currentOrder.equals(newOrder)) {
            if (newOrder > currentOrder) {
                // 순서를 뒤로 미루는 경우
                boardCategoryRepository.decrementOrderAfter(groupId, currentOrder);
            } else if (newOrder < currentOrder) {
                // 순서를 앞으로 당기는 경우
                boardCategoryRepository.incrementOrderAfter(groupId, newOrder);
            }

            // 해당 게시판의 순서 업데이트
            boardCategory.setOrderIndex(newOrder);
            boardCategoryRepository.save(boardCategory);
        }

        // 3. 응답 DTO 생성
        BoardOrderUpdateResponse.BoardData data = BoardOrderUpdateResponse.BoardData.builder()
                .id(boardCategory.getId())
                .type(boardCategory.getBoardType().getDescription())
                .name(boardCategory.getName())
                .order(boardCategory.getOrderIndex())
                .build();

        return BoardOrderUpdateResponse.builder()
                .message("게시판 순서를 변경했습니다.")
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public BoardDeleteResponse deleteBoard(Long groupId, Long boardId) {
        log.info("Deleting board: {} for group: {}", boardId, groupId);

        // 1. 게시판 존재 여부 및 권한 확인
        BoardCategory boardCategory = boardCategoryRepository.findByIdAndStudyGroup_Id(boardId, groupId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. Board ID: " + boardId + ", Group ID: " + groupId));

        // 2. 게시판 삭제 (soft delete)
        boardCategory.setIsDeleted(true);
        boardCategoryRepository.save(boardCategory);

        // 3. 다른 게시판들의 순서 조정
        boardCategoryRepository.decrementOrderAfter(groupId, boardCategory.getOrderIndex());

        // 4. 응답 DTO 생성
        BoardDeleteResponse.BoardData data = BoardDeleteResponse.BoardData.builder()
                .id(boardCategory.getId())
                .type(boardCategory.getBoardType().getDescription())
                .name(boardCategory.getName())
                .deletedAt(LocalDateTime.now())
                .build();

        return BoardDeleteResponse.builder()
                .message("게시판을 삭제했습니다.")
                .data(data)
                .build();
    }
}