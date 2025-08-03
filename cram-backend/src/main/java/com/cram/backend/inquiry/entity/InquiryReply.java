package com.cram.backend.inquiry.entity;

import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiry_reply")
@ToString
public class InquiryReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity admin;

    @Column(name = "reply_content", nullable = false, columnDefinition = "TEXT")
    private String replyContent;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Builder
    public InquiryReply(Inquiry inquiry, UserEntity admin, String replyContent) {
        this.inquiry = inquiry;
        this.admin = admin;
        this.replyContent = replyContent;
    }

    // 비즈니스 메서드
    public void updateReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public boolean belongsToInquiry(Long inquiryId) {
        return this.inquiry.getId().equals(inquiryId);
    }

    public boolean isWrittenByAdmin(Long adminId) {
        return this.admin.getId().equals(adminId);
    }

    public boolean belongsToUser(Long userId) {
        return this.inquiry.getUser().getId().equals(userId);
    }

    public String getInquiryTitle() {
        return this.inquiry.getTitle();
    }

    public String getInquiryContent() {
        return this.inquiry.getContent();
    }

    public String getAdminName() {
        return this.admin.getName();
    }

    public String getInquirerName() {
        return this.inquiry.getUser().getName();
    }

    public Long getInquirerId() {
        return this.inquiry.getUser().getId();
    }

    public boolean isRecentReply() {
        // 최근 7일 내 답변인지 확인
        return this.createAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public boolean canBeModified() {
        // 답변 등록 후 24시간 내에만 수정 가능
        return this.createAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    public void markInquiryAsCompleted() {
        this.inquiry.markAsCompleted();
    }
}