package com.cram.backend.like.service;

import com.cram.backend.board.entity.Post;
import com.cram.backend.board.entity.PostLike;
import com.cram.backend.board.repository.PostRepository;
import com.cram.backend.like.repository.PostLikeRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            postLikeRepository.deleteByPostAndUser(post, user);
        } else {
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(postLike);
        }
    }

    public void removeLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!postLikeRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("좋아요가 존재하지 않습니다.");
        }

        postLikeRepository.deleteByPostAndUser(post, user);
    }
}