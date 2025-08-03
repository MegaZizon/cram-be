package com.cram.backend.like.repository;

import com.cram.backend.board.entity.Post;
import com.cram.backend.board.entity.PostLike;
import com.cram.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndUser(Post post, UserEntity user);
    void deleteByPostAndUser(Post post, UserEntity user);
}