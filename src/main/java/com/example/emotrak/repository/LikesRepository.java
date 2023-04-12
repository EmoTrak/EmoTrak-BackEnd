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

    void deleteByUserAndDaily(User user, Daily daily);

    Optional<Likes> findByUserAndComment(User user, Comment comment);

    void deleteByUserAndComment(User user, Comment comment);

    void deleteAllByComment(Comment comment);

    void deleteAllByUser(User user);

    @Modifying
    @Query(value = " DELETE FROM likes "
                 + "  WHERE comment_id IN ("
                 + "                         select c.id as comment_id "
                 + "                           from comment c "
                 + "                          where c.daily_id = :dailyId)"
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
                 + "                         select c.id as comment_id "
                 + "                           from comment c "
                 + "                          where c.user_id = :userId)"
                 , nativeQuery = true)
    void deleteCommentLikeByUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = " DELETE FROM likes "
                 + "  WHERE board_id IN ("
                 + "                         select c.id as board_id "
                 + "                           from board c "
                 + "                          where c.user_id = :userId)"
                 , nativeQuery = true)
    void deleteBoardLikeByUser(@Param("userId") Long userId);

}
