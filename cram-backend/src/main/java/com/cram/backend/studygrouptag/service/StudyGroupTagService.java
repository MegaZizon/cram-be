package com.cram.backend.studygrouptag.service;

import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.studygrouptag.entity.StudyGroupTag;

import java.util.List;

public interface StudyGroupTagService {
    List<StudyGroupTag> updateTagsToGroup(StudyGroup group, List<String> tagNames);
    List<String> getTagNamesByGroupId(Long groupId);
    List<StudyGroupTag> syncTagsToGroup(StudyGroup group, List<String> tagNames);
}
