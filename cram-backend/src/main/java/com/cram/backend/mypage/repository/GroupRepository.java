package com.cram.backend.mypage.repository;

import com.cram.backend.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<StudyGroup, Long> {

    /**
     * 특정 사용자가 속한 스터디 그룹들 조회 (GroupMember를 통해)
     */
    @Query("SELECT sg FROM StudyGroup sg JOIN GroupMember gm ON sg.id = gm.studyGroup.id " +
            "WHERE gm.user.id = :userId AND gm.isActive = true")
    List<StudyGroup> findByUserId(@Param("userId") Long userId);

    @Query("SELECT gm.studyGroup.id FROM GroupMember gm WHERE gm.user.id = :userId AND gm.isActive = true")
    List<Long> findActiveStudyGroupIdsByUserId(@Param("userId") Long userId);

    List<StudyGroup> findByIdIn(List<Long> ids);


    @Query("SELECT gm.studyGroup.id, COUNT(gm) " +
                "FROM GroupMember gm " +
                "WHERE gm.studyGroup.id IN :groupIds AND gm.isActive = true " +
                "GROUP BY gm.studyGroup.id")
    List<Object[]> countActiveMembersByStudyGroupIds(@Param("groupIds") List<Long> groupIds);


}