package com.cram.backend.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_attachment")
@ToString
public class PostAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "original_name", nullable = false, columnDefinition = "TEXT")
    private String originalName;

    @Column(name = "saved_name", nullable = false, columnDefinition = "TEXT")
    private String savedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostAttachment(String filePath, String originalName, String savedName, Post post) {
        this.filePath = filePath;
        this.originalName = originalName;
        this.savedName = savedName;
        this.post = post;
    }

    // 비즈니스 메서드
    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean belongsToPost(Long postId) {
        return this.post.getId().equals(postId);
    }

    public boolean isImageFile() {
        String fileName = this.originalName.toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".webp");
    }

    public boolean isDocumentFile() {
        String fileName = this.originalName.toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".doc") ||
                fileName.endsWith(".docx") || fileName.endsWith(".txt") ||
                fileName.endsWith(".hwp");
    }

    public String getFileExtension() {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
    }

    public String getDisplayName() {
        // 원본 파일명이 너무 길면 줄여서 표시
        if (originalName.length() > 50) {
            return originalName.substring(0, 47) + "...";
        }
        return originalName;
    }
}