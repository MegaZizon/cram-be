package com.cram.backend.board.repository;

import com.cram.backend.board.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {

    /**
     * 특정 스터디 그룹에 속한 게시판들을 순서대로 조회
     */
    List<BoardCategory> findByStudyGroup_IdOrderByOrderIndexAsc(Long studyGroupId);

    /**
     * 특정 스터디 그룹에 속한 특정 게시판 조회
     */
    Optional<BoardCategory> findByIdAndStudyGroup_Id(Long boardId, Long studyGroupId);

    /**
     * 특정 스터디 그룹에 속한 게시판 개수 조회
     */
    long countByStudyGroup_Id(Long studyGroupId);

    /**
     * 특정 스터디 그룹에서 특정 순서보다 큰 게시판들의 순서를 1씩 증가
     */
    @Modifying
    @Query("UPDATE BoardCategory bc SET bc.orderIndex = bc.orderIndex + 1 WHERE bc.studyGroup.id = :studyGroupId AND bc.orderIndex >= :orderIndex")
    void incrementOrderAfter(@Param("studyGroupId") Long studyGroupId, @Param("orderIndex") Integer orderIndex);

    /**
     * 특정 스터디 그룹에서 특정 순서보다 큰 게시판들의 순서를 1씩 감소
     */
    @Modifying
    @Query("UPDATE BoardCategory bc SET bc.orderIndex = bc.orderIndex - 1 WHERE bc.studyGroup.id = :studyGroupId AND bc.orderIndex > :orderIndex")
    void decrementOrderAfter(@Param("studyGroupId") Long studyGroupId, @Param("orderIndex") Integer orderIndex);

    /**
     * 특정 게시판의 순서 업데이트
     */
    @Modifying
    @Query("UPDATE BoardCategory bc SET bc.orderIndex = :newOrder WHERE bc.id = :boardId")
    void updateBoardOrder(@Param("boardId") Long boardId, @Param("newOrder") Integer newOrder);

    /**
     * 특정 스터디 그룹에서 가장 큰 순서 값 조회
     */
    @Query("SELECT COALESCE(MAX(bc.orderIndex), 0) FROM BoardCategory bc WHERE bc.studyGroup.id = :studyGroupId")
    Integer findMaxOrderByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 특정 카테고리명과 스터디 그룹으로 게시판 존재 여부 확인
     */
    boolean existsByNameAndStudyGroup_Id(String name, Long studyGroupId);

    /**
     * 삭제되지 않은 게시판들만 조회
     */
    List<BoardCategory> findByStudyGroup_IdAndIsDeletedFalseOrderByOrderIndexAsc(Long studyGroupId);
}