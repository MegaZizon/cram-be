package com.cram.backend.joingroup.entity;

import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_join_request")
public class GroupJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String greeting;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupJoinStatus status;

    @Column(length = 255)
    private String reason;

    @Builder
    public GroupJoinRequest(Long id, UserEntity user, StudyGroup studyGroup, String nickname, String greeting, LocalDateTime requestedAt, GroupJoinStatus status, String reason) {
        this.id = id;
        this.user = user;
        this.studyGroup = studyGroup;
        this.nickname = nickname;
        this.greeting = greeting;
        this.requestedAt = requestedAt;
        this.status = status;
        this.reason = reason;
    }
}
