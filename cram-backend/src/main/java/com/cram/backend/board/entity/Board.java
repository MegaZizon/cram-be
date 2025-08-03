package com.cram.backend.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
@ToString
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_id", nullable = false)
    private BoardCategory boardCategory;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Builder
    public Board(Long id, BoardCategory boardCategory, String name) {
        this.id = id;
        this.boardCategory = boardCategory;
        this.name = name;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }

    public boolean belongsToCategory(Long categoryId) {
        return this.boardCategory.getId().equals(categoryId);
    }

    public boolean belongsToStudyGroup(Long studyGroupId) {
        return this.boardCategory.getStudyGroup().getId().equals(studyGroupId);
    }
}

// BoardCategory 엔티티에도 Board와의 관계를 추가해야 합니다
// BoardCategory.java에 다음 필드와 메서드를 추가:
/*
@OneToMany(mappedBy = "boardCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Board> boards = new ArrayList<>();

public Board getBoard() {
    return boards.isEmpty() ? null : boards.get(0);
}
*/