package com.example.emotrak.repository;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = " SELECT c.id, c.comment, DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i:%s') AS created_at "
                 + "      , c.user_id = :userId AS auth, u.nickname "
                 + "      , COALESCE(l.count, 0) count, l2.id IS NOT NULL AS has_like, r.id IS NOT NULL AS has_report "
                 + "   FROM comment c "
                 + "   LEFT JOIN users u ON c.user_id = u.id "
                 + "   LEFT JOIN ( "
                 + "                SELECT comment_id, COUNT(*) count "
                 + "                FROM likes "
                 + "                GROUP BY comment_id "
                 + "             ) l ON c.id = l.comment_id "
                 + "   LEFT JOIN likes l2 ON c.id = l2.comment_id AND l2.user_id = :userId "
                 + "   LEFT JOIN report r ON c.id = r.comment_id AND r.user_id = :userId "
                 + "  WHERE c.daily_id = :dailyId"
                 + "  ORDER BY c.created_at"
                 , nativeQuery = true)
    List<Object[]> getCommentDetail(@Param("userId") Long userId, @Param("dailyId") Long dailyId, Pageable pageable);

    void deleteAllByUser(User user);

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
