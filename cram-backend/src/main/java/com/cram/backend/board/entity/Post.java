package com.cram.backend.board.entity;

import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_id", nullable = false)
    private BoardCategory boardCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

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

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Builder
    public Post(BoardCategory boardCategory, Board board, UserEntity user,
                String title, String content) {
        // id, createdAt은 JPA가 자동 처리하므로 빌더에서 제외
        this.boardCategory = boardCategory;
        this.board = board;
        this.user = user;
        this.title = title;
        this.content = content;
        this.isDeleted = false;  // 기본값 직접 설정
        this.viewCount = 0;      // 기본값 직접 설정
    }

    // 비즈니스 메서드
    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isActive() {
        return !this.isDeleted;
    }

    public boolean isWrittenBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean belongsToBoard(Long boardId) {
        return this.board.getId().equals(boardId);
    }

    public boolean belongsToCategory(Long categoryId) {
        return this.boardCategory.getId().equals(categoryId);
    }
}