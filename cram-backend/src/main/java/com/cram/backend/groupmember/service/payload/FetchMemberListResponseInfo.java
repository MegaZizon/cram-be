package com.cram.backend.groupmember.service.payload;

import com.cram.backend.joingroup.dto.PageableInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FetchMemberListResponseInfo {
    private List<FetchMemberResponseInfo> memberList;
    private PageableInfo pageableInfo;
}
