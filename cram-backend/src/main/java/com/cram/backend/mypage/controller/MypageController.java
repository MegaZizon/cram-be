package com.cram.backend.mypage.controller;


import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.mypage.dto.*;
import com.cram.backend.mypage.service.MypageService;
import com.cram.backend.user.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    // 1. 내 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<GetUserProfileResponseDto> getUserProfile(
            @AuthenticationPrincipal CustomOAuth2User user,
            @ModelAttribute GetUserProfileRequestDto requestDto) {

        Long userId = user.getUserId();
        return ResponseEntity.ok(mypageService.getUserProfile(userId, requestDto));
    }

    // 2. 내 그룹 목록 조회
    @GetMapping("/groups")
    public ResponseEntity<GetMyGroupListResponseDto> getMyGroupList(
            @AuthenticationPrincipal CustomOAuth2User user,
            @ModelAttribute GetMyGroupListRequestDto requestDto) {

        Long userId = user.getUserId();
        return ResponseEntity.ok(mypageService.getMyGroupList(userId, requestDto));
    }

    // 3. 내 문의 내역 조회
    @GetMapping("/inquiry")
    public ResponseEntity<GetMyInquiryResponseDto> getMyInquiries(
            @AuthenticationPrincipal CustomOAuth2User user,
            @ModelAttribute GetMyInquiryRequestDto requestDto) {

        Long userId = user.getUserId();
        return ResponseEntity.ok(mypageService.getMyInquiries(userId, requestDto));
    }

    // 4. 내 답변 내역 조회
    @GetMapping("/inquiry-reply")
    public ResponseEntity<GetMyInquiryReplyResponseDto> getMyInquiryReplies(
            @AuthenticationPrincipal CustomOAuth2User user,
            @ModelAttribute GetMyInquiryReplyRequestDto requestDto) {

        Long userId = user.getUserId();
        return ResponseEntity.ok(mypageService.getMyInquiryReplies(userId, requestDto));
    }
}