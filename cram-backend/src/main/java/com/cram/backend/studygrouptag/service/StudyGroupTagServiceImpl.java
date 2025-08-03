package com.cram.backend.studygrouptag.service;

import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygrouptag.entity.StudyGroupTag;
import com.cram.backend.studygrouptag.repository.StudyGroupTagRepository;
import com.cram.backend.tag.entity.Tag;
import com.cram.backend.tag.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupTagServiceImpl implements StudyGroupTagService {

    private final StudyGroupTagRepository studyGroupTagRepository;
    private final TagService tagService;

    @Override
    public List<StudyGroupTag> updateTagsToGroup(StudyGroup group, List<String> tagNames) {
        List<Tag> tags = tagService.findOrCreateTags(tagNames);
        return tags.stream()
                .map(tag -> {
                    StudyGroupTag studyGroupTag = StudyGroupTag.builder()
                            .studyGroup(group)
                            .tag(tag)
                            .build();
                    return studyGroupTagRepository.save(studyGroupTag);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTagNamesByGroupId(Long groupId) {
        return studyGroupTagRepository.findAllByStudyGroupId(groupId).stream()
                .map(studyGroupTag -> studyGroupTag.getTag().getName())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudyGroupTag> syncTagsToGroup(StudyGroup group, List<String> tagNames) {
        // 현재 매핑된 태그 조회
        List<StudyGroupTag> existing = studyGroupTagRepository.findAllByStudyGroupId(group.getId());
        Set<String> existingNames = existing.stream()
                .map(m -> m.getTag().getName())
                .collect(Collectors.toSet());
        Set<String> requested = new HashSet<>(tagNames != null ? tagNames : List.of());

        // 삭제할 매핑
        List<StudyGroupTag> toDelete = existing.stream()
                .filter(m -> !requested.contains(m.getTag().getName()))
                .collect(Collectors.toList());
        if (!toDelete.isEmpty()) {
            studyGroupTagRepository.deleteAll(toDelete);
        }

        // 추가할 태그 이름
        Set<String> toAddNames = requested.stream()
                .filter(name -> !existingNames.contains(name))
                .collect(Collectors.toSet());

        List<Tag> toAddTags = tagService.findOrCreateTags(List.copyOf(toAddNames));
        for (Tag toAddTag : toAddTags) {
            studyGroupTagRepository.save(
                    StudyGroupTag.builder()
                            .studyGroup(group)
                            .tag(toAddTag)
                            .build()
            );
        }

        return studyGroupTagRepository.findAllByStudyGroupId(group.getId());
    }
}
