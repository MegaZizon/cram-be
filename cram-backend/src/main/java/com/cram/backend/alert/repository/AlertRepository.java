package com.cram.backend.alert.repository;

import com.cram.backend.alert.entity.Alert;
import com.cram.backend.alert.enums.AlertCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    /**
     * 특정 사용자의 최근 2주 내 알림 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId " +
            "AND a.createdAt >= :startDate " +
            "ORDER BY a.createdAt DESC")
    Page<Alert> findRecentAlertsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    /**
     * 특정 사용자의 모든 알림 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId " +
            "ORDER BY a.createdAt DESC")
    Page<Alert> findAllAlertsByUserId(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * 특정 사용자의 안읽은 알림 목록 조회
     */
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId " +
            "AND a.isRead = false " +
            "ORDER BY a.createdAt DESC")
    List<Alert> findUnreadAlertsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 안읽은 알림 개수 조회
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user.id = :userId " +
            "AND a.isRead = false")
    long countUnreadAlertsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 특정 알림 조회 (권한 확인용)
     */
    @Query("SELECT a FROM Alert a WHERE a.id = :alertId " +
            "AND a.user.id = :userId")
    Optional<Alert> findByIdAndUserId(
            @Param("alertId") Long alertId,
            @Param("userId") Long userId);

    /**
     * 특정 사용자의 모든 안읽은 알림을 읽음 처리
     */
    @Modifying
    @Query("UPDATE Alert a SET a.isRead = true WHERE a.user.id = :userId " +
            "AND a.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    /**
     * 특정 알림을 읽음 처리
     */
    @Modifying
    @Query("UPDATE Alert a SET a.isRead = true WHERE a.id = :alertId")
    int markAsReadById(@Param("alertId") Long alertId);

    /**
     * 특정 카테고리의 알림 목록 조회
     */
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId " +
            "AND a.category = :category " +
            "AND a.createdAt >= :startDate " +
            "ORDER BY a.createdAt DESC")
    Page<Alert> findAlertsByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") AlertCategory category,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    /**
     * 읽음 여부별 알림 목록 조회
     */
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId " +
            "AND a.isRead = :isRead " +
            "AND a.createdAt >= :startDate " +
            "ORDER BY a.createdAt DESC")
    Page<Alert> findAlertsByUserIdAndReadStatus(
            @Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    /**
     * 특정 사용자의 특정 기간 내 알림 개수 조회
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user.id = :userId " +
            "AND a.createdAt BETWEEN :startDate AND :endDate")
    long countAlertsByUserIdInPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 오래된 알림들 조회 (일정 기간 이후 삭제용)
     */
    @Query("SELECT a FROM Alert a WHERE a.createdAt < :cutoffDate")
    List<Alert> findOldAlerts(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 특정 사용자의 알림 모두 삭제
     */
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 읽은 알림들 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.user.id = :userId AND a.isRead = true")
    int deleteReadAlertsByUserId(@Param("userId") Long userId);

    /**
     * 특정 기간보다 오래된 알림들 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.createdAt < :cutoffDate")
    int deleteOldAlerts(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 알림이 특정 사용자에게 속하는지 확인
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Alert a WHERE a.id = :alertId AND a.user.id = :userId")
    boolean existsByIdAndUserId(
            @Param("alertId") Long alertId,
            @Param("userId") Long userId);

    /**
     * 최근 활성 사용자들의 알림 통계
     */
    @Query("SELECT a.user.id, COUNT(a) as alertCount " +
            "FROM Alert a WHERE a.createdAt >= :startDate " +
            "GROUP BY a.user.id " +
            "ORDER BY alertCount DESC")
    List<Object[]> getAlertStatsByUser(@Param("startDate") LocalDateTime startDate);

    /**
     * 카테고리별 알림 통계
     */
    @Query("SELECT a.category, COUNT(a) as categoryCount " +
            "FROM Alert a WHERE a.createdAt >= :startDate " +
            "GROUP BY a.category " +
            "ORDER BY categoryCount DESC")
    List<Object[]> getAlertStatsByCategory(@Param("startDate") LocalDateTime startDate);
}