package com.cram.backend.inquiry.repository;

import com.cram.backend.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // Inquiry 엔티티는 user 필드를 가지고 있으므로 이것은 정상 작동함
    List<Inquiry> findByUser_IdOrderByCreatedAtDesc(Long userId);
}