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
@Table(name = "comment")
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Builder
    public Comment(UserEntity user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.likeCount = 0;  // 기본값 설정
    }

    // 비즈니스 메서드
    public void updateContent(String content) {
        this.content = content;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public boolean isWrittenBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean belongsToPost(Long postId) {
        return this.post.getId().equals(postId);
    }

    public boolean hasLikes() {
        return this.likeCount > 0;
    }

    public String getAuthorName() {
        return this.user.getName();
    }

    public String getPostTitle() {
        return this.post.getTitle();
    }
}