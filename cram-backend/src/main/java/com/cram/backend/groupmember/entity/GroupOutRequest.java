package com.cram.backend.groupmember.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_out_request")
public class GroupOutRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private GroupMember member;

    @Column(name = "leave_at")
    private LocalDateTime leaveAt;

    @Column(columnDefinition = "TEXT", name = "leave_reason")
    private String leaveReason;

    @Builder
    public GroupOutRequest(Long id, GroupMember member, LocalDateTime leaveAt, String leaveReason) {
        this.id = id;
        this.member = member;
        this.leaveAt = leaveAt;
        this.leaveReason = leaveReason;
    }
}
