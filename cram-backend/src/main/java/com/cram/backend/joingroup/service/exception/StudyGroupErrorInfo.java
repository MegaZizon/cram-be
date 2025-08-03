package com.cram.backend.joingroup.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyGroupErrorInfo {
    ALREADY_PROCESSED_REQUEST(400, "이미 처리된 가입 신청입니다."),
    NOT_VALID_STUDY_GROUP_ACCESS_TYPE(400, "스터디 그룹의 공개 유형이 잘못 되었습니다."),
    ALREADY_LEAVE_REQUEST(400, "해당 유저는 이미 탈퇴 신청을 했습니다."),
    CANNOT_MODIFY_MEMBER_LIMIT(400, "현재 멤버 수가 수정할 멤버 수 제한보다 많습니다."),
    ONLY_LEADER_ACCESSIBLE(403, "그룹 리더만 요청을 처리할 수 있습니다."),
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다."),
    STUDY_GROUP_NOT_FOUND(404, "해당 스터디 그룹을 찾을 수 없습니다."),
    JOIN_REQUEST_NOT_FOUND(404, "해당 그룹 가입 요청을 찾을 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND(404, "해당 스터디 그룹의 멤버가 아니거나, 멤버를 찾을 수 없습니다."),
    ALREADY_MEMBER(409, "이미 이 그룹의 멤버로 가입되어 있습니다."),
    ALREADY_REQUESTED(409, "이미 이 그룹에 가입 요청을 보냈습니다.");

    private final int errorCode;
    private final String errorMessage;
}
