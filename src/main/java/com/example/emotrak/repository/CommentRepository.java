package com.example.emotrak.repository;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT new com.example.emotrak.dto.comment.CommentDetailResponseDto(c, u, COUNT(l), COUNT(userLikes), COUNT(r)) " +
            "FROM Comment c " +
            "JOIN c.user u " +
            "LEFT JOIN Likes l ON l.comment = c " +
            "LEFT JOIN Likes userLikes ON userLikes.comment = c AND userLikes.user = :user " +
            "LEFT JOIN Report r ON r.comment = c AND r.user = :user " +
            "WHERE c.daily = :daily " +
            "GROUP BY c " +
            "ORDER BY c.createdAt ASC")
    Page<CommentDetailResponseDto> findAllCommentDetailResponseDtoByDailyAndUser(@Param("daily") Daily daily, @Param("user") User user, Pageable pageable);

    @Modifying
    @Query(value = " DELETE FROM comment "
                 + "  WHERE user_id = :userId"
                 , nativeQuery = true)
    void deleteAllByUser(@Param("userId") Long userId);


    @Modifying
    @Query(value = " DELETE FROM comment "
                 + "  WHERE daily_id = :dailyId"
                 , nativeQuery = true)
    void deleteByDaily(@Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM comment "
                 + "  WHERE daily_id IN ("
                 + "                         select id as daily_id "
                 + "                           from daily "
                 + "                          where user_id = :userId)"
                 , nativeQuery = true)
    void deleteByUser(@Param("userId") Long userId);

}
