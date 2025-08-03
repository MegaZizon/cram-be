package com.cram.backend.user.repository;

import com.cram.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    /**
     * 이메일로 사용자 조회
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 사용자명으로 사용자 조회
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 제공자 ID로 사용자 조회
     */
    Optional<UserEntity> findByProviderId(String providerId);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsername(String username);

    /**
     * 제공자 ID 존재 여부 확인
     */
    boolean existsByProviderId(String providerId);

    /**
     * 모든 사용자 조회 (삭제 관련 필드가 없으므로 단순 조회)
     */
    List<UserEntity> findAllByOrderByIdDesc();

    /**
     * 특정 스터디 그룹에 속한 사용자들 조회
     */
    @Query("SELECT u FROM UserEntity u JOIN GroupMember gm ON u.id = gm.user.id WHERE gm.studyGroup.id = :studyGroupId AND gm.isActive = true")
    List<UserEntity> findByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 특정 스터디 그룹의 리더 조회
     */
    @Query("SELECT u FROM UserEntity u JOIN GroupMember gm ON u.id = gm.user.id WHERE gm.studyGroup.id = :studyGroupId " +
            "AND gm.role = 'LEADER' AND gm.isActive = true")
    Optional<UserEntity> findLeaderByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 닉네임으로 사용자 검색 (부분 일치)
     */
    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE %:username%")
    List<UserEntity> searchByUsername(@Param("username") String username);

    /**
     * 사용자의 게시글 수 조회
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId AND p.isDeleted = false")
    long countPostsByUserId(@Param("userId") Long userId);

    /**
     * 사용자의 댓글 수 조회 (Comment 엔티티의 isDeleted 필드가 없으므로 단순 카운트)
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);

    /**
     * 사용자가 참여한 스터디 그룹 수 조회
     */
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.user.id = :userId AND gm.isActive = true")
    long countStudyGroupsByUserId(@Param("userId") Long userId);
}