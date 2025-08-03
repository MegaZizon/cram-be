package com.cram.backend.alert.entity;

import com.cram.backend.alert.enums.GroupAlertCategory;
import com.cram.backend.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_alert")
@ToString
public class GroupAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 10)
    private GroupAlertCategory category;

    @Builder
    public GroupAlert(Alert alert, StudyGroup studyGroup, GroupAlertCategory category) {
        this.alert = alert;
        this.studyGroup = studyGroup;
        this.category = category;
    }

    // 비즈니스 메서드
    public boolean belongsToAlert(Long alertId) {
        return this.alert.getId().equals(alertId);
    }

    public boolean belongsToGroup(Long groupId) {
        return this.studyGroup.getId().equals(groupId);
    }

    public boolean isAcceptAlert() {
        return this.category == GroupAlertCategory.ACCEPT;
    }

    public boolean isFailAlert() {
        return this.category == GroupAlertCategory.FAIL;
    }

    public boolean isCommentAlert() {
        return this.category == GroupAlertCategory.COMMENT;
    }

    public boolean isSummaryAlert() {
        return this.category == GroupAlertCategory.SUMMARY;
    }

    public String getCategoryDisplayName() {
        return switch (this.category) {
            case ACCEPT -> "승인";
            case FAIL -> "거절";
            case COMMENT -> "댓글";
            case SUMMARY -> "요약";
        };
    }

    public String getGroupName() {
        return this.studyGroup.getName();
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
}