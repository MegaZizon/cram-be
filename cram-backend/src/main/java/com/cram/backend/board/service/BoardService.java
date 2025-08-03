package com.cram.backend.board.service;

import com.cram.backend.board.dto.*;

public interface BoardService {

    /**
     * 스터디 그룹에 새로운 게시판 생성
     */
    BoardCreateResponse createBoard(Long groupId, BoardCreateRequest request);

    /**
     * 특정 스터디 그룹의 게시판 목록 조회
     */
    StudyGroupBoardListResponse getStudyGroupBoardList(Long groupId);

    /**
     * 특정 게시판 수정
     */
    BoardEditResponse editBoard(Long groupId, Long boardId, BoardEditRequest request);

    /**
     * 특정 게시판 순서 변경
     */
    BoardOrderUpdateResponse updateBoardOrder(Long groupId, Long boardId, BoardOrderUpdateRequest request);

    /**
     * 특정 게시판 삭제
     */
    BoardDeleteResponse deleteBoard(Long groupId, Long boardId);
}