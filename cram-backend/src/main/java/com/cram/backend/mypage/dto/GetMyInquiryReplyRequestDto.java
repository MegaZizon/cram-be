package com.cram.backend.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetMyInquiryReplyRequestDto {

    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long id;
}
