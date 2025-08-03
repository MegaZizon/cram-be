package com.cram.backend.joingroup.service;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.groupmember.entity.GroupMemberRole;
import com.cram.backend.groupmember.repository.GroupMemberRepository;
import com.cram.backend.joingroup.dto.PageableInfo;
import com.cram.backend.joingroup.entity.GroupJoinRequest;
import com.cram.backend.joingroup.entity.GroupJoinStatus;
import com.cram.backend.joingroup.repository.JoinGroupRepository;
import com.cram.backend.joingroup.service.dto.*;
import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinGroupServiceImpl implements JoinGroupService {

    private final JoinGroupRepository joinGroupRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public JoinGroupRequestCreatedInfo joinGroup(JoinGroupRequestInfo requestInfo) {
        Long userId = requestInfo.getUserId();
        Long groupId = requestInfo.getGroupId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.USER_NOT_FOUND));

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        if (groupMemberRepository.existsByUser_IdAndStudyGroup_IdAndIsActiveTrue(userId, groupId)) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ALREADY_MEMBER);
        }

        if (joinGroupRepository.existsByUser_IdAndStudyGroup_Id(userId, groupId)) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ALREADY_REQUESTED);
        }

        GroupJoinRequest joinRequest = GroupJoinRequest.builder()
                .user(user)
                .studyGroup(studyGroup)
                .nickname(requestInfo.getNickname())
                .greeting(requestInfo.getGreetingMessage())
                .requestedAt(LocalDateTime.now())
                .status(GroupJoinStatus.PENDING)
                .reason(null)
                .build();

        GroupJoinRequest savedEntity = joinGroupRepository.save(joinRequest);

        return new JoinGroupRequestCreatedInfo(
                savedEntity.getId(),
                savedEntity.getStatus().name().toLowerCase(),
                savedEntity.getRequestedAt()
        );
    }

    public boolean existsNicknameInGroup(Long groupId, String nickname) {
        if (!studyGroupRepository.existsById(groupId)) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND);
        }

        return !groupMemberRepository.existsByStudyGroup_IdAndNickname(groupId, nickname);
    }

    public JoinGroupApprovalsListData fetchJoinGroupRequests(Long groupId, Long userId, Pageable pageable) {
        GroupMember member = groupMemberRepository
                .findByUser_IdAndStudyGroup_IdAndIsActiveTrue(userId, groupId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.GROUP_MEMBER_NOT_FOUND));

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getLeader().getId() != member.getId()) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ONLY_LEADER_ACCESSIBLE);
        }

        Page<GroupJoinRequest> pageResult = joinGroupRepository
                .findByStudyGroup_IdAndStatusOrderByIdDesc(
                        groupId,
                        GroupJoinStatus.PENDING,
                        pageable
        );

        List<JoinGroupApprovalsListData.JoinGroupRequestSummary> contentDtoList = pageResult.stream()
                .map(JoinGroupApprovalsListData.JoinGroupRequestSummary::from)
                .collect(Collectors.toList());

        int currentPage    = pageResult.getNumber() + 1;
        int pageSize       = pageResult.getSize();
        long totalElements = pageResult.getTotalElements();
        int totalPages     = pageResult.getTotalPages();

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(currentPage)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        return new JoinGroupApprovalsListData(contentDtoList, pageableInfo);
    }

    @Override
    public ApproveGroupJoinResponseInfo approveGroupJoinRequest(ApproveGroupJoinRequestInfo requestInfo) {
        Long requestId = requestInfo.getRequestId();
        Long userId = requestInfo.getUserId();
        Long groupId = requestInfo.getGroupId();

        GroupJoinRequest groupJoinRequest = joinGroupRepository.findById(requestId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.JOIN_REQUEST_NOT_FOUND));

        if (groupJoinRequest.getStatus() != GroupJoinStatus.PENDING) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ALREADY_PROCESSED_REQUEST);
        }

        groupJoinRequest.setStatus(GroupJoinStatus.APPROVED);
        GroupJoinRequest savedJoinRequest = joinGroupRepository.save(groupJoinRequest);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.USER_NOT_FOUND));

        StudyGroup studyGroup = studyGroupRepository.findById(groupId).
                orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        Long leaderId = studyGroup.getLeader().getUser().getId();
        if (leaderId != userId) throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ONLY_LEADER_ACCESSIBLE);

        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .studyGroup(studyGroup)
                .role(GroupMemberRole.MEMBER)
                .nickname(savedJoinRequest.getNickname())
                .greeting(savedJoinRequest.getGreeting())
                .joinedAt(LocalDateTime.now())
                .isActive(true)
                .kicked(false)
                .build();

        GroupMember savedMember = groupMemberRepository.save(groupMember);
        return ApproveGroupJoinResponseInfo.from(groupJoinRequest, user, studyGroup, savedMember);
    }

    @Override
    public RejectGroupJoinResponseInfo rejectGroupJoinRequest(RejectGroupJoinRequestInfo requestInfo) {
        Long requestId = requestInfo.getRequestId();
        Long groupId = requestInfo.getGroupId();
        Long userId = requestInfo.getUserId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId).
                orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        GroupJoinRequest groupJoinRequest = joinGroupRepository.findById(requestId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.JOIN_REQUEST_NOT_FOUND));

        if (groupJoinRequest.getStatus() != GroupJoinStatus.PENDING) {
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ALREADY_PROCESSED_REQUEST);
        }

        Long leaderId = studyGroup.getLeader().getUser().getId();
        if (leaderId != userId) throw new StudyGroupRuntimeException(StudyGroupErrorInfo.ONLY_LEADER_ACCESSIBLE);

        groupJoinRequest.setStatus(GroupJoinStatus.REJECTED);
        groupJoinRequest.setReason(requestInfo.getReason());
        GroupJoinRequest savedJoinRequest = joinGroupRepository.save(groupJoinRequest);

        return RejectGroupJoinResponseInfo.from(savedJoinRequest, studyGroup, userId, LocalDateTime.now());
    }

}
