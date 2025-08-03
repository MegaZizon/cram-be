package com.cram.backend.studygroup.service.payload;

import com.cram.backend.joingroup.dto.PageableInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyGroupListResponseInfo {
    private List<GetStudyGroupListItemInfo> groupList;
    private PageableInfo pageable;
}
