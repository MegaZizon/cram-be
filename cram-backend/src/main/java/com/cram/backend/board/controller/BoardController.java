package com.cram.backend.board.controller;

import com.cram.backend.board.dto.*;
import com.cram.backend.board.service.BoardService;
import com.cram.backend.board.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}")
@RequiredArgsConstructor
@Tag(name = "Board API", description = "스터디 그룹 게시판 관리 API")
public class BoardController {


    private final BoardService boardService;
    private final PostService postService;

    @Operation(summary = "게시판 생성", description = "스터디 그룹에 새로운 게시판을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시판 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "스터디 그룹을 찾을 수 없음")
    })
    @PostMapping(value = "/boards", params = "!id")
    public ResponseEntity<BoardCreateResponse> createBoard(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @RequestBody BoardCreateRequest request) {
        BoardCreateResponse response = boardService.createBoard(groupId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "게시글 작성", description = "특정 게시판에 새로운 게시글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음")
    })
    @PostMapping("/posts")
    public ResponseEntity<PostCreateResponse> createPost(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @RequestBody PostCreateRequest request) {
        PostCreateResponse response = postService.createPost(groupId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "게시판 목록 조회", description = "특정 스터디 그룹의 게시판 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "스터디 그룹을 찾을 수 없음")
    })
    @GetMapping("/boards")
    public ResponseEntity<StudyGroupBoardListResponse> getStudyGroupBoardList(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId) {
        StudyGroupBoardListResponse response = boardService.getStudyGroupBoardList(groupId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시판 수정", description = "특정 게시판의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음")
    })
    @PutMapping("/boards")
    public ResponseEntity<BoardEditResponse> editBoard(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시판 ID", example = "1") @RequestParam("id") Long boardId,
            @RequestBody BoardEditRequest request) {
        BoardEditResponse response = boardService.editBoard(groupId, boardId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시판 순서 변경", description = "특정 게시판의 순서를 변경합니다.")
    @PostMapping(value = "/boards", params = "id")
    public ResponseEntity<BoardOrderUpdateResponse> updateBoardOrder(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시판 ID", example = "1") @RequestParam("id") Long boardId,
            @RequestBody BoardOrderUpdateRequest request) {
        BoardOrderUpdateResponse response = boardService.updateBoardOrder(groupId, boardId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시판 삭제", description = "특정 게시판을 삭제합니다.")
    @DeleteMapping("/boards")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시판 ID", example = "1") @RequestParam("id") Long boardId) {
        BoardDeleteResponse response = boardService.deleteBoard(groupId, boardId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    @GetMapping("/posts")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID", example = "1") @RequestParam("id") Long postId) {
        PostDetailResponse response = postService.getPostDetail(groupId, postId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정", description = "특정 게시글의 내용을 수정합니다.")
    @PutMapping("/posts")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID", example = "1") @RequestParam("id") Long postId,
            @RequestBody PostUpdateRequest request) {
        PostUpdateResponse response = postService.updatePost(groupId, postId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    @PatchMapping("/posts")
    public ResponseEntity<PostDeleteResponse> deletePost(
            @Parameter(description = "스터디 그룹 ID", example = "1") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID", example = "1") @RequestParam("id") Long postId) {
        PostDeleteResponse response = postService.deletePost(groupId, postId);
        return ResponseEntity.ok(response);
    }
}