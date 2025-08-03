package com.cram.backend.board.repository;

import com.cram.backend.board.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     */
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    /**
     * 특정 사용자가 특정 게시글에 누른 좋아요 조회
     */
    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);

    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    long countByPost_Id(Long postId);

    /**
     * 특정 사용자가 누른 모든 좋아요 조회
     */
    List<PostLike> findByUser_IdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 게시글에 좋아요를 누른 모든 사용자 조회
     */
    @Query("SELECT pl.user FROM PostLike pl WHERE pl.post.id = :postId ORDER BY pl.createdAt DESC")
    List<Object> findUsersByPostId(@Param("postId") Long postId);

    /**
     * 특정 스터디 그룹의 모든 게시글에 대한 좋아요 조회
     */
    @Query("SELECT pl FROM PostLike pl JOIN pl.post p JOIN p.boardCategory bc WHERE bc.studyGroup.id = :studyGroupId " +
            "ORDER BY pl.createdAt DESC")
    List<PostLike> findByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 특정 사용자가 특정 스터디 그룹에서 누른 좋아요들 조회
     */
    @Query("SELECT pl FROM PostLike pl JOIN pl.post p JOIN p.boardCategory bc WHERE pl.user.id = :userId " +
            "AND bc.studyGroup.id = :studyGroupId ORDER BY pl.createdAt DESC")
    List<PostLike> findByUserIdAndStudyGroupId(@Param("userId") Long userId, @Param("studyGroupId") Long studyGroupId);

    /**
     * 가장 많은 좋아요를 받은 게시글들 조회
     */
    @Query("SELECT pl.post, COUNT(pl) as likeCount FROM PostLike pl GROUP BY pl.post ORDER BY likeCount DESC")
    List<Object[]> findMostLikedPosts();

    /**
     * 특정 사용자가 가장 많이 좋아요를 누른 게시판 조회
     */
    @Query("SELECT p.boardCategory, COUNT(pl) as likeCount FROM PostLike pl JOIN pl.post p WHERE pl.user.id = :userId " +
            "GROUP BY p.boardCategory ORDER BY likeCount DESC")
    List<Object[]> findMostLikedBoardsByUser(@Param("userId") Long userId);

    /**
     * 특정 게시글에 좋아요를 누른 사용자 ID 목록
     */
    @Query("SELECT pl.user.id FROM PostLike pl WHERE pl.post.id = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);

    /**
     * 좋아요 삭제 (사용자가 좋아요 취소)
     */
    void deleteByPost_IdAndUser_Id(Long postId, Long userId);

    /**
     * 특정 게시글의 모든 좋아요 삭제 (게시글 삭제 시)
     */
    void deleteByPost_Id(Long postId);

    /**
     * 특정 사용자의 모든 좋아요 삭제 (사용자 탈퇴 시)
     */
    void deleteByUser_Id(Long userId);
}