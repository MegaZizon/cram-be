package com.cram.backend.board.entity;

import com.cram.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
@ToString
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public CommentLike(Comment comment, UserEntity user) {
        this.comment = comment;
        this.user = user;
    }

    // 비즈니스 메서드
    public boolean isLikedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean belongsToComment(Long commentId) {
        return this.comment.getId().equals(commentId);
    }

    public boolean isSameUser(UserEntity user) {
        return this.user.getId().equals(user.getId());
    }

    public boolean isSameComment(Comment comment) {
        return this.comment.getId().equals(comment.getId());
    }

    public String getUserName() {
        return this.user.getName();
    }

    public String getCommentContent() {
        return this.comment.getContent();
    }

    public Long getCommentAuthorId() {
        return this.comment.getUser().getId();
    }
}