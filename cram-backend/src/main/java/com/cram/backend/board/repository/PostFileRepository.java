package com.cram.backend.board.repository;

import com.cram.backend.board.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostFileRepository extends JpaRepository<PostAttachment, Long> {

    /**
     * 특정 게시글에 첨부된 파일들 조회
     */
    List<PostAttachment> findByPostIdOrderByIdAsc(Long postId);

    /**
     * 저장된 파일명으로 파일 조회
     */
    Optional<PostAttachment> findBySavedName(String savedName);

    /**
     * 원본 파일명으로 파일 검색
     */
    List<PostAttachment> findByOriginalNameContaining(String originalName);

    /**
     * 특정 사용자가 업로드한 파일들 조회
     */
    @Query("SELECT pa FROM PostAttachment pa JOIN pa.post p WHERE p.user.id = :userId ORDER BY pa.id DESC")
    List<PostAttachment> findByUploaderId(@Param("userId") Long userId);

    /**
     * 특정 스터디 그룹의 모든 파일들 조회
     */
    @Query("SELECT pa FROM PostAttachment pa JOIN pa.post p JOIN p.boardCategory bc WHERE bc.studyGroup.id = :studyGroupId ORDER BY pa.id DESC")
    List<PostAttachment> findByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 특정 게시글의 파일 개수 조회
     */
    long countByPostId(Long postId);

    /**
     * 고아 파일 조회 (게시글이 삭제된 파일들)
     */
    @Query("SELECT pa FROM PostAttachment pa WHERE pa.post.isDeleted = true")
    List<PostAttachment> findOrphanFiles();

    /**
     * 이미지 파일들만 조회 (확장자 기반)
     */
    @Query("SELECT pa FROM PostAttachment pa WHERE " +
            "LOWER(pa.originalName) LIKE '%.jpg' OR " +
            "LOWER(pa.originalName) LIKE '%.jpeg' OR " +
            "LOWER(pa.originalName) LIKE '%.png' OR " +
            "LOWER(pa.originalName) LIKE '%.gif' OR " +
            "LOWER(pa.originalName) LIKE '%.webp' " +
            "ORDER BY pa.id DESC")
    List<PostAttachment> findImageFiles();

    /**
     * 문서 파일들만 조회 (확장자 기반)
     */
    @Query("SELECT pa FROM PostAttachment pa WHERE " +
            "LOWER(pa.originalName) LIKE '%.pdf' OR " +
            "LOWER(pa.originalName) LIKE '%.doc' OR " +
            "LOWER(pa.originalName) LIKE '%.docx' OR " +
            "LOWER(pa.originalName) LIKE '%.txt' OR " +
            "LOWER(pa.originalName) LIKE '%.hwp' " +
            "ORDER BY pa.id DESC")
    List<PostAttachment> findDocumentFiles();
}