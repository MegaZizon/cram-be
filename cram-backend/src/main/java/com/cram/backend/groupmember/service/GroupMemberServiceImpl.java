package com.cram.backend.groupmember.service;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.groupmember.entity.GroupMemberRole;
import com.cram.backend.groupmember.entity.GroupOutRequest;
import com.cram.backend.groupmember.repository.GroupMemberRepository;
import com.cram.backend.groupmember.repository.GroupOutRequestRepository;
import com.cram.backend.groupmember.service.payload.*;
import com.cram.backend.joingroup.dto.PageableInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberServiceImpl implements  GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupOutRequestRepository groupOutRequestRepository;
    private final UserRepository userRepository;

    @Override
    public LeaveGroupResponseInfo leaveGroup(LeaveGroupRequestInfo requestInfo) {
        Long groupId = requestInfo.getGroupId();
        Long memberId = requestInfo.getMemberId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId).
                orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        if (groupOutRequestRepository.existsByMember_Id(memberId)) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ALREADY_LEAVE_REQUEST);
        }

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.GROUP_MEMBER_NOT_FOUND));

        // TODO: 그룹의 리더가 탈퇴신청 하는 경우에는, 에러 반환

        LocalDateTime leaveAt = LocalDateTime.now();
        member.updateToLeft(leaveAt, requestInfo.getLeaveReason());
        GroupMember savedMember = groupMemberRepository.save(member);

        GroupOutRequest groupOutRequest = GroupOutRequest.builder()
                .member(savedMember)
                .leaveAt(leaveAt)
                .leaveReason(requestInfo.getLeaveReason())
                .build();
        GroupOutRequest savedRequest = groupOutRequestRepository.save(groupOutRequest);
        return new LeaveGroupResponseInfo(savedRequest.getId(), savedRequest.getLeaveAt(), savedRequest.getLeaveReason());
    }

    @Override
    public FetchMemberListResponseInfo fetchMemeberList(FetchMemberListRequestInfo requestInfo) {
        Long groupId = requestInfo.getGroupId();
        if (!studyGroupRepository.existsById(groupId)) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND);
        }

        Page<GroupMember> pages = null;
        if (requestInfo.getNickname().isEmpty()) {
            pages = groupMemberRepository.findByStudyGroup_IdAndIsActiveTrue(requestInfo.getGroupId(), requestInfo.getPageable());
        } else {
            String nickname = requestInfo.getNickname().orElse(null);
            pages = groupMemberRepository.findByStudyGroup_IdAndIsActiveTrueAndNicknameContainingIgnoreCase(
                    requestInfo.getGroupId(),
                    nickname,
                    requestInfo.getPageable()
            );
        }

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(pages.getNumber() + 1)
                .size(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .build();

        List<FetchMemberResponseInfo> memberList = pages.stream()
                .map(FetchMemberResponseInfo::from)  // static factory 메서드 :contentReference[oaicite:1]{index=1}
                .toList();

        return new FetchMemberListResponseInfo(memberList, pageableInfo);
    }

    @Override
    public KickMemberResponseInfo kickMember(KickMemberRequestInfo requestInfo) {
        Long memberId = requestInfo.getMemberId();
        Long groupId = requestInfo.getGroupId();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId).
                orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.GROUP_MEMBER_NOT_FOUND));

        // TODO: JWT 토큰으로 받은 데이터로, 관리자 여부 검증
        LocalDateTime kickedAt = LocalDateTime.now();
        member.updateToKicked(kickedAt, requestInfo.getReason());

        GroupMember savedMember = groupMemberRepository.save(member);
        return KickMemberResponseInfo.builder()
                .memberId(savedMember.getId())
                .kickedAt(savedMember.getLeaveAt())
                .reason(requestInfo.getReason())
                .build();
    }

    @Override
    public GroupMember setLeaderToGroup(Long leaderId, StudyGroup group) {
        UserEntity leaderUserEntity = userRepository.findById(leaderId)
                .orElseThrow(() -> new StudyGroupRuntimeException(USER_NOT_FOUND));

        // TODO: 그룹장 닉네임 설정 필요
        String nickname = "스터디 그룹장";
        GroupMember leader = GroupMember.builder()
                .user(leaderUserEntity)
                .studyGroup(group)
                .role(GroupMemberRole.LEADER)
                .nickname(nickname)
                .build();
        return groupMemberRepository.save(leader);
    }

    @Override
    public Integer getGroupMemberCount(Long groupId) {
        return groupMemberRepository.countMembersByStudyGroupIdAndIsActiveTrue(groupId);
    }
}
