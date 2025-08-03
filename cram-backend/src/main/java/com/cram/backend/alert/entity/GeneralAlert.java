package com.cram.backend.alert.entity;

import com.cram.backend.alert.enums.GeneralAlertCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "general_alert")
@ToString
public class GeneralAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private GeneralAlertCategory category;

    @Builder
    public GeneralAlert(Alert alert, GeneralAlertCategory category) {
        this.alert = alert;
        this.category = category;
    }

    // 비즈니스 메서드
    public boolean belongsToAlert(Long alertId) {
        return this.alert.getId().equals(alertId);
    }

    public boolean isInquiryReplyAlert() {
        return this.category == GeneralAlertCategory.INQUIRY_REPLY;
    }

    public String getCategoryDisplayName() {
        return switch (this.category) {
            case INQUIRY_REPLY -> "문의 답변";
        };
    }

    public String getAlertMessage() {
        return this.alert.getMessage();
    }

    public boolean isAlertRead() {
        return this.alert.getIsRead();
    }

    public Long getAlertUserId() {
        return this.alert.getUser().getId();
    }

    public boolean belongsToUser(Long userId) {
        return this.alert.belongsToUser(userId);
    }

    public void markAlertAsRead() {
        this.alert.markAsRead();
    }

    public void markAlertAsUnread() {
        this.alert.markAsUnread();
    }

    public boolean isRecentAlert() {
        return this.alert.isRecentAlert();
    }
}