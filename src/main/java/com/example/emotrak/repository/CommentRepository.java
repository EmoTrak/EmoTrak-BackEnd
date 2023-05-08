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
    @Query("SELECT new com.example.emotrak.dto.comment.CommentDetailResponseDto(" +
            "c, " +
            "u.id, " +
            "COUNT(l), " +
            "COUNT(userLikes), " +
            "COUNT(r)) " +
            "FROM Comment c " +
            "LEFT JOIN users u ON u = :user " +
            "LEFT JOIN Likes l ON l.comment = c " +
            "LEFT JOIN Likes userLikes ON userLikes.comment = c AND userLikes.user = :user " +
            "LEFT JOIN Report r ON r.comment = c AND r.user = :user " +
            "WHERE c.daily = :daily " +
            "GROUP BY c, u " +
            "ORDER BY c.createdAt ASC")
    /* 매 페이지마다 전체 결과를 정렬한 뒤에 해당 페이지에 맞는 결과를 반환하는 방식으로 동작.
     * 이는 전체 결과가 매우 큰 경우에는 매우 비효율적, 따라서 페이징과 관련하여 불필요한 정렬 작업을 피하기 위해서,
     * createdAt 필드에 인덱스를 추가
     * */
    Page<CommentDetailResponseDto> findAllCommentDetailResponseDtoByDailyAndUser(@Param("daily") Daily daily,
                                                                                @Param("user") User user,
                                                                                       Pageable pageable);

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
