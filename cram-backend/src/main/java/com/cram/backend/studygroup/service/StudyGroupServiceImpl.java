package com.cram.backend.studygroup.service;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.groupmember.service.GroupMemberService;
import com.cram.backend.joingroup.dto.PageableInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo;
import com.cram.backend.joingroup.service.exception.StudyGroupRuntimeException;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.entity.StudyGroupAccessType;
import com.cram.backend.studygroup.entity.payload.EditStudyGroupModel;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.studygroup.service.payload.*;
import com.cram.backend.studygrouptag.service.StudyGroupTagService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cram.backend.joingroup.service.exception.StudyGroupErrorInfo.CANNOT_MODIFY_MEMBER_LIMIT;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberService groupMemberService;
    private final StudyGroupTagService studyGroupTagService;

    @Override
    public CreateStudyGroupResponseInfo createStudyGroup(CreateStudyGroupRequestInfo requestInfo) {
        StudyGroupAccessType accessType = StudyGroupAccessType.from(requestInfo.getAccessType());
        StudyGroup group = StudyGroup.builder()
                .name(requestInfo.getName())
                .description(requestInfo.getDescription())
                .memberLimit(requestInfo.getMemberLimit())
                .accessType(accessType)
                .notice(requestInfo.getGuidelines())
                .build();
        StudyGroup savedGroup = studyGroupRepository.save(group);

        List<String> tags = studyGroupTagService
                .updateTagsToGroup(savedGroup, requestInfo.getTags())
                .stream()
                .map(studyGroupTag -> studyGroupTag.getTag().getName())
                .collect(Collectors.toList());

        GroupMember leader = groupMemberService.setLeaderToGroup(requestInfo.getLeaderId(), savedGroup);
        savedGroup.updateLeader(leader);
        studyGroupRepository.save(savedGroup);

        return CreateStudyGroupResponseInfo.builder()
                .groupId(savedGroup.getId())
                .name(savedGroup.getName())
                .description(savedGroup.getDescription())
                .tags(tags)
                .maxMembers(savedGroup.getMemberLimit())
                .accessType(accessType.name())
                .guidelines(savedGroup.getNotice())
                .createdAt(savedGroup.getCreatedAt())
                .build();
    }

    @Override
    public GetStudyGroupListResponseInfo fetchStudyGroupList(GetStudyGroupListRequestInfo requestInfo) {
        Pageable pageable = requestInfo.getPageable();
        Page<StudyGroup> page = studyGroupRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (requestInfo.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + requestInfo.getName() + "%"));
            }

            String accessType = requestInfo.getAccessType();
            if (accessType != null && accessType.equals("PUBLIC")) {
                predicates.add(cb.equal(root.get("accessType"), StudyGroupAccessType.PUBLIC));
            }

            if (requestInfo.getAccessType() != null) {
                predicates.add(cb.equal(
                        root.get("accessType"),
                        StudyGroupAccessType.valueOf(requestInfo.getAccessType().toUpperCase())
                ));
            }

            if (requestInfo.getKeywords() != null && !requestInfo.getKeywords().isEmpty()) {
                Join<Object, Object> sgTag = root.join("studyGroupTags", JoinType.INNER);
                Join<Object, Object> tag = sgTag.join("tag", JoinType.INNER);
                predicates.add(tag.get("name").in(requestInfo.getKeywords()));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();

        // TODO: post service로부터 호출 할 예정
        Integer postCount = 100;
        // TODO: S3 도입 이후 thumbnail url 반환 예정
        String thumbnailUrl = "image.png";
        List<GetStudyGroupListItemInfo> items = page.getContent().stream()
                .map(group -> {
                    List<String> tags = studyGroupTagService.getTagNamesByGroupId(group.getId());
                    Integer memberCount = groupMemberService.getGroupMemberCount(group.getId());
                    return GetStudyGroupListItemInfo.builder()
                            .groupId(group.getId())
                            .name(group.getName())
                            .description(group.getDescription())
                            .memberCount(memberCount)
                            .postCount(postCount)
                            .accessType(group.getAccessType().name())
                            .tags(tags)
                            .thumbnailUrl(thumbnailUrl)
                            .build();
                })
                .collect(Collectors.toList());

        return GetStudyGroupListResponseInfo.builder()
                .groupList(items)
                .pageable(pageableInfo)
                .build();
    }

    @Override
    public EditStudyGroupResponseInfo editStudyGroup(EditStudyGroupRequestInfo requestInfo) {
        Long groupId = requestInfo.getGroupId();
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new StudyGroupRuntimeException(StudyGroupErrorInfo.STUDY_GROUP_NOT_FOUND));

        int currentMemberCount = groupMemberService.getGroupMemberCount(groupId);
        if (currentMemberCount > requestInfo.getMemberLimit())
            throw new StudyGroupRuntimeException(StudyGroupErrorInfo.CANNOT_MODIFY_MEMBER_LIMIT);

        List<String> tags = studyGroupTagService.syncTagsToGroup(group, requestInfo.getTags()).stream()
                .map(StudyGroupTag -> StudyGroupTag.getTag().getName())
                .toList();

        LocalDateTime updatedAt = LocalDateTime.now();
        group.updateGroup(EditStudyGroupModel.from(requestInfo, updatedAt));
        StudyGroup updatedGroup = studyGroupRepository.save(group);

        return EditStudyGroupResponseInfo.from(updatedGroup, tags);
    }
}
