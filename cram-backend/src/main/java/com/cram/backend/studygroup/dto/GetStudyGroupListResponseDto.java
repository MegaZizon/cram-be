package com.cram.backend.studygroup.dto;

import com.cram.backend.joingroup.dto.PageableInfo;
import com.cram.backend.studygroup.service.payload.GetStudyGroupListItemInfo;
import com.cram.backend.studygroup.service.payload.GetStudyGroupListResponseInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyGroupListResponseDto {

    private List<GetStudyGroupItemDto> content;
    private PageableInfo pageable;

    @Getter
    @Builder
    public static class GetStudyGroupItemDto {
        private Long groupId;
        private String name;
        private String description;
        private Integer memberCount;
        private Integer postCount;
        private String accessType;
        private List<String> tags;
        private String thumbnailUrl;

        public static GetStudyGroupItemDto from(GetStudyGroupListItemInfo itemInfo) {
            return GetStudyGroupItemDto.builder()
                    .groupId(itemInfo.getGroupId())
                    .name(itemInfo.getName())
                    .description(itemInfo.getDescription())
                    .memberCount(itemInfo.getMemberCount())
                    .postCount(itemInfo.getPostCount())
                    .accessType(itemInfo.getAccessType())
                    .tags(itemInfo.getTags())
                    .thumbnailUrl(itemInfo.getThumbnailUrl())
                    .build();
        }
    }

    public static GetStudyGroupListResponseDto from(GetStudyGroupListResponseInfo responseInfo) {
        List<GetStudyGroupItemDto> groupList = responseInfo.getGroupList().stream()
                .map(GetStudyGroupItemDto::from)
                .toList();
        return GetStudyGroupListResponseDto.builder()
                .content(groupList)
                .pageable(responseInfo.getPageable())
                .build();
    }
}
