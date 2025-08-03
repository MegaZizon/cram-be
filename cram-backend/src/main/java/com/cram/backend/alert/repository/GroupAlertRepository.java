package com.cram.backend.alert.repository;

import com.cram.backend.alert.entity.Alert;
import com.cram.backend.alert.entity.GroupAlert;
import com.cram.backend.alert.enums.GroupAlertCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupAlertRepository extends JpaRepository<GroupAlert, Long> {

    /**
     * Alert로 GroupAlert 조회
     */
    Optional<GroupAlert> findByAlert(Alert alert);

    /**
     * Alert ID로 GroupAlert 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.id = :alertId")
    Optional<GroupAlert> findByAlertId(@Param("alertId") Long alertId);

    /**
     * 특정 사용자의 그룹 알림 목록 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findByUserId(@Param("userId") Long userId);

    /**
     * 특정 그룹의 알림 목록 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.studyGroup.id = :groupId " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findByGroupId(@Param("groupId") Long groupId);

    /**
     * 특정 사용자의 특정 그룹 알림 목록 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.studyGroup.id = :groupId " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findByUserIdAndGroupId(
            @Param("userId") Long userId,
            @Param("groupId") Long groupId);

    /**
     * 특정 카테고리의 그룹 알림 목록 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.category = :category " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") GroupAlertCategory category);

    /**
     * 특정 기간 내 그룹 알림 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 읽지 않은 그룹 알림 조회
     */
    @Query("SELECT ga FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.isRead = false " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GroupAlert> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * 읽지 않은 그룹 알림 개수 조회
     */
    @Query("SELECT COUNT(ga) FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 특정 그룹의 읽지 않은 알림 개수 조회
     */
    @Query("SELECT COUNT(ga) FROM GroupAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.studyGroup.id = :groupId " +
            "AND ga.alert.isRead = false")
    long countUnreadByUserIdAndGroupId(
            @Param("userId") Long userId,
            @Param("groupId") Long groupId);
}