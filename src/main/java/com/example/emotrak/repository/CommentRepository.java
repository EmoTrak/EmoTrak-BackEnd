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
    // 게시글에 대한 모든 댓글 검색 (페이지네이션 적용)
    Page<Comment> findAllByDaily(Daily daily, Pageable pageable);

    // 게시글에 대한 댓글 수 계산
    int countByDaily(Daily daily);

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
