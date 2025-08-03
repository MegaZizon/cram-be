package com.cram.backend.studygroup.entity;

import com.cram.backend.groupmember.entity.GroupMember;
import com.cram.backend.studygroup.entity.payload.EditStudyGroupModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private GroupMember leader;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "member_limit")
    private Integer memberLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private StudyGroupAccessType accessType;

    @Column(columnDefinition = "TEXT")
    private String notice;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    public void updateLeader(GroupMember leader) {
        this.leader = leader;
    }

    public void updateGroup(EditStudyGroupModel editModel) {
        this.name = editModel.getName();
        this.description = editModel.getDescription();
        this.memberLimit = editModel.getMemberLimit();
        this.accessType = editModel.getAccessType();
        this.notice = editModel.getNotice();
        this.updatedAt = editModel.getUpdatedAt();
    }

    @Builder
    public StudyGroup(Long id, GroupMember leader, String name, String description, Integer memberLimit, StudyGroupAccessType accessType, String notice, LocalDateTime updatedAt, LocalDateTime createdAt, String link, String thumbnailUrl) {
        this.id = id;
        this.leader = leader;
        this.name = name;
        this.description = description;
        this.memberLimit = memberLimit;
        this.accessType = accessType;
        this.notice = notice;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.link = link;
        this.thumbnailUrl = thumbnailUrl;
    }
}