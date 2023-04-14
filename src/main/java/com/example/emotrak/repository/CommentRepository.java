package com.example.emotrak.repository;

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
    Page<Comment> findAllByDaily(Daily daily, Pageable pageable);

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
