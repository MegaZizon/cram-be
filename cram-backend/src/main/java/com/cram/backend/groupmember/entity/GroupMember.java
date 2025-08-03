package com.cram.backend.groupmember.entity;

import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.user.entity.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "group_member")
@ToString
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GroupMemberRole role;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String greeting;

    @CreationTimestamp
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "leave_at")
    private LocalDateTime leaveAt;

    @Column(name = "leave_reason", columnDefinition = "TEXT")
    private String leaveReason;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean kicked = false;

    public void updateToLeft(LocalDateTime leaveAt, String leaveReason) {
        this.leaveAt = leaveAt;
        this.leaveReason = leaveReason;
        this.isActive = false;
    }

    public void updateToKicked(LocalDateTime kickedAt, @Nullable String kickReason) {
        this.leaveAt = kickedAt;
        this.kicked = true;
        this.isActive = false;
        if (kickReason != null && !kickReason.isBlank())
            this.leaveReason = kickReason;
    }
}
