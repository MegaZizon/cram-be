package com.cram.backend.mypage.service;

import com.cram.backend.inquiry.entity.Inquiry;
import com.cram.backend.inquiry.entity.InquiryReply;
import com.cram.backend.inquiry.repository.InquiryReplyRepository;
import com.cram.backend.inquiry.repository.InquiryRepository;
import com.cram.backend.mypage.dto.*;
import com.cram.backend.mypage.repository.*;
import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygroup.repository.StudyGroupRepository;
import com.cram.backend.studygrouptag.entity.StudyGroupTag;
import com.cram.backend.studygrouptag.repository.StudyGroupTagRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final StudyGroupRepository  studyGroupRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final ObjectMapper objectMapper;
    private final StudyGroupTagRepository studyGroupTagRepository;

    @Override
    public GetUserProfileResponseDto getUserProfile(Long userId, GetUserProfileRequestDto requestDto) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new GetUserProfileResponseDto(
                user.getName(),
                user.getEmail(),
                user.getProvider(),
                user.getProfileImage()
        );
    }

    @Override
    public GetMyGroupListResponseDto getMyGroupList(Long userId, GetMyGroupListRequestDto requestDto) {

        // 1. username 으로 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 사용자의 활성 스터디 그룹 ID들 조회
        List<Long> studyGroupIds = groupRepository.findActiveStudyGroupIdsByUserId(user.getId());

        if (studyGroupIds.isEmpty()) {
            return new GetMyGroupListResponseDto(List.of()); // 빈 응답
        }

        // 3. 그룹 정보 조회
        List<StudyGroup> groups = groupRepository.findByIdIn(studyGroupIds);

        // 4. 각 그룹의 활동 중인 멤버 수 조회
        List<Object[]> countResults = groupRepository.countActiveMembersByStudyGroupIds(studyGroupIds);
        Map<Long, Integer> memberCounts = countResults.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        // 5. ✅ 모든 그룹의 태그를 한 번에 조회 (N+1 문제 해결)
        List<StudyGroupTag> allGroupTags = studyGroupTagRepository.findAllByStudyGroupIdIn(studyGroupIds);

        Map<Long, List<String>> groupTagsMap = allGroupTags.stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getStudyGroup().getId(),
                        Collectors.mapping(
                                tag -> tag.getTag().getName(),
                                Collectors.toList()
                        )
                ));

        // for each 문 형태
//        Map<Long, List<String>> groupTagsMap = new HashMap<>();
//
//        for (StudyGroupTag tag : allGroupTags) {
//            Long groupId = tag.getStudyGroup().getId();
//            String tagName = tag.getTag().getName();
//
//            // 그룹ID가 맵에 없으면 새 리스트 만들기
//            if (!groupTagsMap.containsKey(groupId)) {
//                groupTagsMap.put(groupId, new ArrayList<>());
//            }
//
//            // 태그 이름 추가
//            groupTagsMap.get(groupId).add(tagName);
//        }
//
        // 7. 응답 생성
        List<GetMyGroupListResponseDto.GroupInfo> groupInfos = groups.stream()
                .map(group -> {
                    int currentMembers = memberCounts.getOrDefault(group.getId(), 0);
                    List<String> tagNames = groupTagsMap.getOrDefault(group.getId(), List.of());

                    return new GetMyGroupListResponseDto.GroupInfo(
                            group.getId(),
                            group.getName(),
                            group.getDescription(),
                            currentMembers,
                            group.getAccessType().name(),
                            tagNames,
                            group.getThumbnailUrl()
                    );
                })
                .collect(Collectors.toList());

        return new GetMyGroupListResponseDto(groupInfos);
    }

    @Override
    public GetMyInquiryResponseDto getMyInquiries(Long userId, GetMyInquiryRequestDto requestDto) {

        // 1. username으로 사용자 조회 (스터디 그룹 조회와 동일한 패턴)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 문의사항 조회 (생성일 기준 내림차순)
        List<Inquiry> inquiries = inquiryRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());

        // 3. DTO로 변환
        List<GetMyInquiryResponseDto.InquiryInfo> inquiryInfos = inquiries.stream()
                .map(inquiry -> new GetMyInquiryResponseDto.InquiryInfo(
                        inquiry.getId(),
                        inquiry.getTitle(),
                        inquiry.getContent(),
                        inquiry.getCreatedAt(),
                        inquiry.getStatus().name()
                ))
                .collect(Collectors.toList());

        return new GetMyInquiryResponseDto(inquiryInfos);
    }

    @Override
    public GetMyInquiryReplyResponseDto getMyInquiryReplies(Long userId, GetMyInquiryReplyRequestDto requestDto) {
        // InquiryReply 엔티티에서 해당 사용자의 답변들을 조회
        // 실제로는 inquiry.user.id로 조회해야 함
        List<InquiryReply> replies = inquiryReplyRepository.findByInquiry_User_IdOrderByCreateAtDesc(userId);

        List<GetMyInquiryReplyResponseDto.InquiryReplyInfo> replyInfos = replies.stream()
                .map(reply -> new GetMyInquiryReplyResponseDto.InquiryReplyInfo(
                        reply.getInquiry().getId(),
                        reply.getInquiry().getTitle(),
                        reply.getReplyContent(),
                        reply.getCreateAt(),
                        reply.getAdmin().getId().toString() // adminId를 String으로 변환
                ))
                .collect(Collectors.toList());

        return new GetMyInquiryReplyResponseDto(replyInfos);
    }

    private List<String> parseTagsFromJson(String tagsJson) {
        try {
            if (tagsJson == null || tagsJson.trim().isEmpty()) {
                return List.of();
            }
            return objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // JSON 파싱 실패 시 빈 리스트 반환
            return List.of();
        }
    }
}