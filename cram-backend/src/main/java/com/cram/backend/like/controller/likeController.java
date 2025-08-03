package com.cram.backend.like.controller;

import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.like.service.CommentService;
import com.cram.backend.like.service.PostService;
import com.cram.backend.user.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class likeController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("{group_id}/boards/{board_id}/posts/{post_id}/comments/{comment_id}/likes")
    public ResponseEntity<Void> likeComment(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable("comment_id") Long commentId
    ) {
        Long userId = user.getUserId();
        commentService.toggleLike(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{group_id}/boards/{board_id}/posts/{post_id}/comments/{comment_id}/likes")
    public ResponseEntity<Void> unlikeComment(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable("comment_id") Long commentId
    ) {
        Long userId = user.getUserId();
        commentService.removeLike(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{group_id}/boards/{board_id}/posts/{post_id}/likes")
    public ResponseEntity<Void> likePost(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable("post_id") Long postId
    ) {
        Long userId = user.getUserId();
        postService.toggleLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{group_id}/boards/{board_id}/posts/{post_id}/likes")
    public ResponseEntity<Void> unlikePost(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable("post_id") Long postId
    ) {
        Long userId = user.getUserId();
        postService.removeLike(postId, userId);
        return ResponseEntity.ok().build();
    }
}