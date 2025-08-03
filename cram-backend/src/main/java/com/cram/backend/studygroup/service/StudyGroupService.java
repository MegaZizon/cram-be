package com.cram.backend.studygroup.service;

import com.cram.backend.studygroup.service.payload.*;

public interface StudyGroupService {
    CreateStudyGroupResponseInfo createStudyGroup(CreateStudyGroupRequestInfo requestInfo);
    GetStudyGroupListResponseInfo fetchStudyGroupList(GetStudyGroupListRequestInfo requestInfo);
    EditStudyGroupResponseInfo editStudyGroup(EditStudyGroupRequestInfo requestInfo);
}