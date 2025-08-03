package com.cram.backend.alert.entity;

import com.cram.backend.alert.enums.AlertCategory;
import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alert")
@ToString
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 10)
    private AlertCategory category;

    @Builder
    public Alert(UserEntity user, String message, AlertCategory category) {
        this.user = user;
        this.message = message;
        this.category = category;
        this.isRead = false;  // 기본값 설정
    }

    // 비즈니스 메서드
    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsUnread() {
        this.isRead = false;
    }

    public boolean isUnread() {
        return !this.isRead;
    }

    public boolean belongsToUser(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isGroupAlert() {
        return this.category == AlertCategory.GROUP;
    }

    // 누락된 메서드 추가
    public boolean isGeneralAlert() {
        return this.category == AlertCategory.GENERAL;
    }

    public String getUserName() {
        return this.user.getName();
    }

    public String getCategoryDisplayName() {
        return switch (this.category) {
            case GROUP -> "그룹";
            case GENERAL -> "일반";
        };
    }

    public boolean isRecentAlert() {
        // 최근 24시간 내 알림인지 확인
        return this.createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }
}