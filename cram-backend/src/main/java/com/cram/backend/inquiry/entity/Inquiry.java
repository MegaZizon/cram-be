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
@Table(name = "inquiry")
@ToString
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private InquiryStatus status = InquiryStatus.PROCESSING;

    @Builder
    public Inquiry(UserEntity user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PROCESSING;  // 기본값 설정
    }

    // 비즈니스 메서드
    public void updateInquiry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void markAsCompleted() {
        this.status = InquiryStatus.COMPLETED;
    }

    public void markAsProcessing() {
        this.status = InquiryStatus.PROCESSING;
    }

    public boolean isProcessing() {
        return this.status == InquiryStatus.PROCESSING;
    }

    public boolean isCompleted() {
        return this.status == InquiryStatus.COMPLETED;
    }

    public boolean belongsToUser(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isWrittenBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public String getUserName() {
        return this.user.getName();
    }

    public String getStatusDisplayName() {
        return this.status.getDisplayName();
    }

    public boolean isRecentInquiry() {
        // 최근 7일 내 문의인지 확인
        return this.createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public boolean canBeModified() {
        // 처리중 상태일 때만 수정 가능
        return this.status == InquiryStatus.PROCESSING;
    }

    // Enum 정의
    public enum InquiryStatus {
        PROCESSING("처리중"),
        COMPLETED("답변완료");

        private final String displayName;

        InquiryStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}