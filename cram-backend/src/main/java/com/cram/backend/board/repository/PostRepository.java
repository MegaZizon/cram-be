package com.cram.backend.board.repository;

import com.cram.backend.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 특정 스터디 그룹에 속한 게시글 조회
     */
    Optional<Post> findByIdAndBoardCategory_StudyGroup_Id(Long postId, Long studyGroupId);

    /**
     * 특정 게시판의 게시글 목록을 페이징으로 조회 (삭제되지 않은 것만)
     */
    Page<Post> findByBoardCategory_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long boardCategoryId, Pageable pageable);

    /**
     * 특정 스터디 그룹의 모든 게시글 목록을 페이징으로 조회 (삭제되지 않은 것만)
     */
    Page<Post> findByBoardCategory_StudyGroup_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long studyGroupId, Pageable pageable);

    /**
     * 특정 사용자가 작성한 게시글 목록 조회
     */
    List<Post> findByUser_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 게시판의 게시글 개수 조회 (삭제되지 않은 것만)
     */
    long countByBoardCategory_IdAndIsDeletedFalse(Long boardCategoryId);

    /**
     * 특정 스터디 그룹의 총 게시글 개수 조회 (삭제되지 않은 것만)
     */
    long countByBoardCategory_StudyGroup_IdAndIsDeletedFalse(Long studyGroupId);

    /**
     * 게시글 조회수 증가
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /**
     * 특정 키워드로 게시글 검색 (제목 + 내용)
     */
    @Query("SELECT p FROM Post p WHERE p.boardCategory.studyGroup.id = :studyGroupId AND p.isDeleted = false " +
            "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("studyGroupId") Long studyGroupId,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    /**
     * 인기 게시글 조회 (최신순 - 좋아요 기능이 완전하지 않으므로)
     */
    @Query("SELECT p FROM Post p WHERE p.boardCategory.studyGroup.id = :studyGroupId AND p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("studyGroupId") Long studyGroupId, Pageable pageable);

    /**
     * 특정 사용자가 좋아요한 게시글 목록 조회
     */
    @Query("SELECT p FROM Post p JOIN PostLike pl ON p.id = pl.post.id WHERE pl.user.id = :userId AND p.isDeleted = false " +
            "ORDER BY pl.createdAt DESC")
    Page<Post> findLikedPostsByUser(@Param("userId") Long userId, Pageable pageable);
}