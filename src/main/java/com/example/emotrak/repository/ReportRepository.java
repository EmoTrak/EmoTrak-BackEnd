package com.example.emotrak.repository;

import com.example.emotrak.entity.Report;
import com.example.emotrak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 사용자와 게시물 ID로 신고 정보를 찾는 메서드
    Optional<Report> findByUserAndDailyId(User user, Long dailyId);

    Optional<Report> findByUserAndCommentId(User user, Long commentId);

}
