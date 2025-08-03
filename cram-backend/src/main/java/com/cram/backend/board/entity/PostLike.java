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
@Table(name = "post_like")
@ToString
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public PostLike(Post post, UserEntity user) {
        this.post = post;
        this.user = user;
    }

    // 비즈니스 메서드
    public boolean isLikedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean belongsToPost(Long postId) {
        return this.post.getId().equals(postId);
    }

    public boolean isSameUser(UserEntity user) {
        return this.user.getId().equals(user.getId());
    }

    public boolean isSamePost(Post post) {
        return this.post.getId().equals(post.getId());
    }

    public String getUserName() {
        return this.user.getName();
    }

    public String getPostTitle() {
        return this.post.getTitle();
    }

    public Long getPostAuthorId() {
        return this.post.getUser().getId();
    }

    public boolean isPostAuthor() {
        return this.user.getId().equals(this.post.getUser().getId());
    }
}