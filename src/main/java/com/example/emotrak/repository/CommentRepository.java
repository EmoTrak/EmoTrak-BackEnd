package com.example.emotrak.repository;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 주어진 게시물 ID(dailyId)에 대한 댓글 목록과 각 댓글에 대한 추가 정보(좋아요 수, 사용자의 좋아요 여부, 사용자의 신고 여부)를 Page<CommentDetailResponseDto> 객체로 반환
    @Query(value = "SELECT new com.example.emotrak.dto.comment.CommentDetailResponseDto("
                 + "c, "
                 + "u, "
                 + "CAST((SELECT COUNT(l) FROM Likes l WHERE l.comment.id = c.id) AS int), "
                 + "(SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Likes l WHERE l.user.id = :userId AND l.comment.id = c.id), "
                 + "(SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r WHERE r.user.id = :userId AND r.comment.id = c.id)) "
                 + "FROM Comment c JOIN c.user u WHERE c.daily.id = :dailyId",
      // 쿼리의 전체 결과 수를 반환(게시물 ID(dailyId)와 일치하는 댓글 수를 반환)
      countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.daily.id = :dailyId")
    Page<CommentDetailResponseDto> findAllCommentsWithLikeAndReportByDailyId(@Param("dailyId") Long dailyId,
                                                                            @Param("userId")  Long userId,
                                                                                              Pageable pageable);


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
