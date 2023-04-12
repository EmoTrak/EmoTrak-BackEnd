package com.example.emotrak.repository;

import com.example.emotrak.entity.Report;
import com.example.emotrak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 사용자와 게시물 ID로 신고 정보를 찾는 메서드
    Optional<Report> findByUserAndDailyId(User user, Long dailyId);

    Optional<Report> findByUserAndCommentId(User user, Long commentId);

    @Modifying
    @Query(value = " DELETE FROM report "
                 + "  WHERE comment_id IN ("
                 + "                         select c.id as comment_id "
                 + "                           from comment c "
                 + "                          where daily_id = :dailyId)"
                 , nativeQuery = true)
    void deleteByDaily(@Param("dailyId") Long dailyId);

}
