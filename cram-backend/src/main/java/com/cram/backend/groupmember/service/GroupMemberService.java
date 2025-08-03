package com.cram.backend.groupmember.service;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.groupmember.service.payload.*;
import com.cram.backend.studygroup.entity.StudyGroup;

public interface GroupMemberService {
    LeaveGroupResponseInfo leaveGroup(LeaveGroupRequestInfo requestInfo);
    FetchMemberListResponseInfo fetchMemeberList(FetchMemberListRequestInfo requestInfo);
    KickMemberResponseInfo kickMember(KickMemberRequestInfo requestInfo);
    GroupMember setLeaderToGroup(Long leaderId, StudyGroup group);
    Integer getGroupMemberCount(Long groupId);
}
