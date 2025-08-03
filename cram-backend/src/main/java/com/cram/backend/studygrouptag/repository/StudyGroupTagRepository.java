package com.cram.backend.studygrouptag.repository;

import com.cram.backend.studygrouptag.entity.StudyGroupTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyGroupTagRepository extends JpaRepository<StudyGroupTag, Long> {
    List<StudyGroupTag> findAllByStudyGroupId(Long studyGroupId);

    @Query("SELECT sgt FROM StudyGroupTag sgt JOIN FETCH sgt.tag WHERE sgt.studyGroup.id IN :studyGroupIds")
    List<StudyGroupTag> findAllByStudyGroupIdIn(@Param("studyGroupIds") List<Long> studyGroupIds);
}
