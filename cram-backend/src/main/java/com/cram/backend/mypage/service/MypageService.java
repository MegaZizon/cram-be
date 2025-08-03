package com.cram.backend.mypage.service;

import com.cram.backend.mypage.dto.*;

public interface MypageService {

    GetUserProfileResponseDto getUserProfile(Long userId, GetUserProfileRequestDto requestDto);

    GetMyGroupListResponseDto getMyGroupList( Long userId,GetMyGroupListRequestDto requestDto);

    GetMyInquiryResponseDto getMyInquiries( Long userId,GetMyInquiryRequestDto requestDto);

    GetMyInquiryReplyResponseDto getMyInquiryReplies(Long userId, GetMyInquiryReplyRequestDto requestDto);
}