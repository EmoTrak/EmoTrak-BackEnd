package com.example.emotrak.repository;

import com.example.emotrak.dto.board.BoardDetailResponseDto;
import com.example.emotrak.dto.board.BoardImgRequestDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Daily, Long> {

    // 사용자와 특정 날짜에 해당하는 게시물 수를 검색
    @Query(" SELECT COUNT(d) "
            + "   FROM Daily d "
            + "  WHERE d.user = :user "
            + "    AND d.dailyYear = :year "
            + "    AND d.dailyMonth = :month "
            + "    AND d.dailyDay = :day")
    long countDailyPostsByUserAndDate(@Param("user") User user,
                                      @Param("year") int year,
                                      @Param("month") int month,
                                      @Param("day") int day);
    @Query("select d.imgUrl from Daily d where d.user = :user")
    List<String> findImgUrlByUser(@Param("user") User user);

    @Query("SELECT new com.example.emotrak.dto.board.BoardDetailResponseDto(d, u, COUNT(l), COUNT(userLikes), COUNT(r), (SELECT COUNT(c) FROM Comment c WHERE c.daily = d)) " +
            "FROM Daily d " +
            "JOIN d.user u " +
            "LEFT JOIN Likes l ON l.daily = d " +
            "LEFT JOIN Likes userLikes ON userLikes.daily = d AND userLikes.user = :user " +
            "LEFT JOIN Report r ON r.daily = d AND r.user = :user " +
            "WHERE d.id = :id " +
            "GROUP BY d, u")
    BoardDetailResponseDto findBoardDetailResponseDtoByIdAndUser(@Param("id") Long id, @Param("user") User user);




    @Modifying
    @Query(value = " DELETE FROM daily "
            + "  WHERE user_id = :userId"
            , nativeQuery = true)
    void deleteAllByUser(@Param("userId") Long userId);

    @Query(value = " SELECT new com.example.emotrak.dto.board.BoardImgRequestDto(d.id, d.imgUrl, d.user.nickname, d.emotion.id)"
            + "   FROM Daily d"
            + "  WHERE d.emotion.id in (:emo)"
            + "    AND d.share = true "
            + "  ORDER BY d.createdAt desc ")
    Page<BoardImgRequestDto> getBoardImagesRecent(@Param("emo") List<Long> emoList, Pageable pageable);

    @Query(value = " SELECT new com.example.emotrak.dto.board.BoardImgRequestDto(d.id, d.imgUrl, d.user.nickname, d.emotion.id)"
            + "   FROM Daily d left join Likes l on d.id = l.daily.id"
            + "  WHERE d.emotion.id in (:emo)"
            + "    AND d.share = true "
            + "  GROUP BY d.id, d.imgUrl"
            + "  ORDER BY count(l.daily.id)  desc, d.createdAt desc")
    Page<BoardImgRequestDto> getBoardImagesPopular(@Param("emo") List<Long> emoList, Pageable pageable);

    @Query(value = " SELECT new com.example.emotrak.dto.board.BoardImgRequestDto(d.id, d.imgUrl, d.user.nickname, d.emotion.id)"
            + "   FROM Daily d"
            + "  WHERE d.user.id = :userId"
            + "  ORDER BY d.createdAt desc")
    Page<BoardImgRequestDto> getBoardImagesMine(Long userId, Pageable pageable);
}
