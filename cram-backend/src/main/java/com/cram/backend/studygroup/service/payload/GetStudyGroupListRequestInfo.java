package com.cram.backend.studygroup.service.payload;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Builder
public class GetStudyGroupListRequestInfo {
    Pageable pageable;
    String name;
    List<String> keywords;
    String accessType;
}
