package com.example.emotrak.repository;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Likes;
import com.example.emotrak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndDaily(User user, Daily daily);

    int countByDaily(Daily daily);

    int countByComment(Comment comment);

    Optional<Likes> findByUserAndComment(User user, Comment comment);

    void deleteByUserAndComment(User user, Comment comment);

    void deleteAllByComment(Comment comment);

    void deleteAllByUser(User user);

    @Modifying
    @Query(value = " DELETE FROM likes "
            + "  WHERE comment_id IN ("
            + "                         select id as comment_id "
            + "                           from comment "
            + "                          where daily_id = :dailyId)"
            , nativeQuery = true)
    void deleteCommentLike(@Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM likes "
            + "  WHERE daily_id = :dailyId"
            , nativeQuery = true)
    void deleteBoardLike(@Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM likes "
            + "  WHERE comment_id IN ("
            + "                         select id as comment_id "
            + "                           from comment "
            + "                          where user_id = :userId)"
            , nativeQuery = true)
    void deleteCommentLikeByUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = " DELETE FROM likes "
            + "  WHERE daily_id IN ("
            + "                         select id as daily_id "
            + "                           from daily "
            + "                          where user_id = :userId);"
            + " DELETE FROM likes"
            + "  WHERE id IN ("
            + "                   SELECT likes.id FROM ("
            + "                           SELECT l.id"
            + "                             FROM likes l"
            + "                            INNER JOIN comment c ON l.comment_id = c.id"
            + "                            INNER JOIN daily d ON c.daily_id = d.id"
            + "                            WHERE d.user_id = :userId"
            + "                       ) likes )"
            , nativeQuery = true)
    void deleteByUser(@Param("userId") Long userId);

}
