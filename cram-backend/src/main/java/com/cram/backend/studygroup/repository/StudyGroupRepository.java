package com.cram.backend.studygroup.repository;

import com.cram.backend.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> {

    /**
     * 스터디 그룹명으로 조회
     */
    Optional<StudyGroup> findByName(String name);

    /**
     * 스터디 그룹명 존재 여부 확인
     */
    boolean existsByName(String name);

    /**
     * 현재 활성 멤버 수 조회 (GroupMember 테이블과 조인하여 계산)
     */
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.studyGroup.id = :studyGroupId AND gm.isActive = true")
    int getCurrentMemberCount(@Param("studyGroupId") Long studyGroupId);

    /**
     * 리더 권한 확인
     */
    @Query("SELECT CASE WHEN sg.leader.user.id = :userId THEN true ELSE false END FROM StudyGroup sg WHERE sg.id = :studyGroupId")
    boolean isLeader(@Param("studyGroupId") Long studyGroupId, @Param("userId") Long userId);

    /**
     * 그룹 멤버 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm " +
            "WHERE gm.studyGroup.id = :studyGroupId AND gm.user.id = :userId AND gm.isActive = true")
    boolean isMember(@Param("studyGroupId") Long studyGroupId, @Param("userId") Long userId);

    /**
     * 그룹이 가득 찼는지 확인
     */
    @Query("SELECT CASE WHEN (SELECT COUNT(gm) FROM GroupMember gm WHERE gm.studyGroup.id = :studyGroupId AND gm.isActive = true) >= sg.memberLimit " +
            "THEN true ELSE false END FROM StudyGroup sg WHERE sg.id = :studyGroupId")
    boolean isGroupFull(@Param("studyGroupId") Long studyGroupId);

    // 다른 복잡한 쿼리들은 필요시 추후 구현
    // 현재는 기본적인 CRUD와 멤버 관련 메서드만 활성화
}