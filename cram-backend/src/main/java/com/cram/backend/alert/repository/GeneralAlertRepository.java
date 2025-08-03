package com.cram.backend.alert.repository;

import com.cram.backend.alert.entity.Alert;
import com.cram.backend.alert.entity.GeneralAlert;
import com.cram.backend.alert.enums.GeneralAlertCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralAlertRepository extends JpaRepository<GeneralAlert, Long> {

    /**
     * Alert로 GeneralAlert 조회
     */
    Optional<GeneralAlert> findByAlert(Alert alert);

    /**
     * Alert ID로 GeneralAlert 조회
     */
    @Query("SELECT ga FROM GeneralAlert ga WHERE ga.alert.id = :alertId")
    Optional<GeneralAlert> findByAlertId(@Param("alertId") Long alertId);

    /**
     * 특정 사용자의 일반 알림 목록 조회
     */
    @Query("SELECT ga FROM GeneralAlert ga WHERE ga.alert.user.id = :userId " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GeneralAlert> findByUserId(@Param("userId") Long userId);

    /**
     * 특정 카테고리의 일반 알림 목록 조회
     */
    @Query("SELECT ga FROM GeneralAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.category = :category " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GeneralAlert> findByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") GeneralAlertCategory category);

    /**
     * 특정 기간 내 일반 알림 조회
     */
    @Query("SELECT ga FROM GeneralAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GeneralAlert> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 읽지 않은 일반 알림 조회
     */
    @Query("SELECT ga FROM GeneralAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.isRead = false " +
            "ORDER BY ga.alert.createdAt DESC")
    List<GeneralAlert> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * 읽지 않은 일반 알림 개수 조회
     */
    @Query("SELECT COUNT(ga) FROM GeneralAlert ga WHERE ga.alert.user.id = :userId " +
            "AND ga.alert.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
}