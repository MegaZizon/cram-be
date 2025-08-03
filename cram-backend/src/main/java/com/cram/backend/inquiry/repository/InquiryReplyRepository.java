package com.cram.backend.inquiry.repository;

import com.cram.backend.inquiry.entity.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
    // 사용자가 문의한 것에 대한 답변들을 조회 (inquiry.user.id로 접근)
    // 그리고 createAt이 맞음 (InquiryReply 엔티티의 필드명 확인)
    List<InquiryReply> findByInquiry_User_IdOrderByCreateAtDesc(Long userId);
}