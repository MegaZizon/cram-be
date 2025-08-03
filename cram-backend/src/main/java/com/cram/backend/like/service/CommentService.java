package com.cram.backend.like.service;

import com.cram.backend.board.entity.Comment;
import com.cram.backend.board.entity.CommentLike;
import com.cram.backend.board.repository.CommentRepository;
import com.cram.backend.like.repository.CommentLikeRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void toggleLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        } else {
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(commentLike);
        }
    }

    public void removeLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("좋아요가 존재하지 않습니다.");
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}