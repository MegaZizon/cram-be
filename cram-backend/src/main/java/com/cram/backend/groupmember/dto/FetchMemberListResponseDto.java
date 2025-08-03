package com.cram.backend.groupmember.dto;

import com.cram.backend.joingroup.dto.PageableInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FetchMemberListResponseDto {
    private List<FetchMemberResponseDto> content;
    private PageableInfo pageable;
}
