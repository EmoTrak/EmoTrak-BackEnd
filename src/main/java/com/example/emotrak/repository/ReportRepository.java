package com.example.emotrak.repository;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
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

    void deleteAllByDaily(Daily daily);

    void deleteAllByComment(Comment comment);

    @Modifying
    @Query(value = " DELETE FROM report "
            + "  WHERE user_id = :userId"
            , nativeQuery = true)
    void deleteAllByUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = " DELETE FROM report "
            + "  WHERE comment_id IN ("
            + "                         select id as comment_id "
            + "                           from comment "
            + "                          where daily_id = :dailyId)"
            , nativeQuery = true)
    void deleteCommentByDaily(@Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM report "
            + "  WHERE comment_id IN ("
            + "                         select id as comment_id "
            + "                           from comment "
            + "                          where user_id = :userId)"
            , nativeQuery = true)
    void deleteCommentLikeByUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = " DELETE FROM report "
            + "  WHERE daily_id IN ("
            + "                         select id as daily_id "
            + "                           from daily "
            + "                          where user_id = :userId)"
            , nativeQuery = true)
    void deleteByUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = " DELETE FROM report"
            + "  WHERE id IN ("
            + "                   SELECT report.id FROM ("
            + "                           SELECT r.id"
            + "                             FROM report r"
            + "                            INNER JOIN comment c ON r.comment_id = c.id"
            + "                            INNER JOIN daily d ON c.daily_id = d.id"
            + "                            WHERE d.user_id = :userId"
            + "                       ) report )"
            , nativeQuery = true)
    void deleteByUserComment(@Param("userId") Long userId);

}
