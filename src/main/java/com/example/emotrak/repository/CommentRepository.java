package com.example.emotrak.repository;

import com.example.emotrak.dto.comment.CommentDetailDto;
import com.example.emotrak.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = " SELECT c.id AS id, c.comment AS comment"
                 + "      , DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt "
                 + "      , IF (c.user_id = :userId, 'true', 'false') AS hasAuth, u.nickname AS nickname "
                 + "      , COALESCE(l.count, 0) AS likesCnt, IF (l2.id, 'true', 'false') AS hasLike"
                 + "      , IF (r.id, 'true', 'false') AS hasReport "
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
                 + "  ORDER BY c.created_at",
            countQuery = " SELECT count(c.id) "
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
//    Page<CommentDetailDto> getCommentDetail(@Param("userId") Long userId, @Param("dailyId") Long dailyId, Pageable pageable);
    Page<Object[]> getCommentDetail(@Param("userId") Long userId, @Param("dailyId") Long dailyId, Pageable pageable);

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
