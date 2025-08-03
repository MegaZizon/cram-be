package com.cram.backend.board.entity;

import com.cram.backend.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_category")
@ToString
public class BoardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false, length = 10)
    private BoardType boardType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 추가 필드들
    @Column(name = "description", columnDefinition = "TEXT")
    private String description = "";

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Builder
    public BoardCategory(Long id, StudyGroup studyGroup, BoardType boardType, String name, String description, Integer orderIndex) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.boardType = boardType;
        this.name = name;
        this.description = description != null ? description : "";
        this.orderIndex = orderIndex != null ? orderIndex : 0;
        this.isDeleted = false;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }

    // 누락된 메서드들 추가
    public void updateBoard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public BoardType getType() {
        return this.boardType;
    }

    public boolean belongsToCategory(Long categoryId) {
        return this.id.equals(categoryId);
    }

    public boolean belongsToStudyGroup(Long studyGroupId) {
        return this.studyGroup.getId().equals(studyGroupId);
    }

    public boolean isStudyType() {
        return this.boardType == BoardType.STUDY;
    }

    public boolean isCommunicationType() {
        return this.boardType == BoardType.COMMUNICATION;
    }

    // Enum 정의
    public enum BoardType {
        STUDY("학습"),
        COMMUNICATION("소통");

        private final String description;

        BoardType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}