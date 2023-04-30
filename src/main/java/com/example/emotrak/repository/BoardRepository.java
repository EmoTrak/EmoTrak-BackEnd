package com.example.emotrak.repository;

import com.example.emotrak.dto.board.BoardGetDetailDto;
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
import java.util.Optional;

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


    @Query(value = " SELECT d.share AS share, d.user_id AS userId, d.id AS dailyId "
                 + "      , DATE_FORMAT(d.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt "
                 + "      , d.emotion_id AS emotionId, d.star AS star, d.detail AS detail, d.img_url AS imgUrl "
                 + "      , IF(d.user_id = :userId, 'true', 'false') AS auth "
                 + "      , u.nickname AS nickname, l.count AS likeCount, d.has_restrict AS hasRestrict "
                 + "      , IF(l2.has_like, 'true', 'false') AS hasLike, d.draw AS draw "
                 + "      , IF(r.has_report, 'true', 'false') AS hasReport, c.count AS commentCount  "
                 + "   FROM daily d "
                 + "        JOIN users u ON d.user_id = u.id "
                 + "        JOIN (SELECT count(*) count FROM likes WHERE daily_id = :dailyId) l "
                 + "        JOIN (SELECT count(*) count FROM comment WHERE daily_id = :dailyId) c "
                 + "   LEFT JOIN (SELECT true has_like FROM likes WHERE daily_id = :dailyId AND user_id = :userId) l2 "
                 + "              ON l2.has_like IS NOT NULL "
                 + "   LEFT JOIN (SELECT true has_report FROM report WHERE daily_id = :dailyId AND user_id = :userId) r "
                 + "              ON r.has_report IS NOT NULL "
                 + "  WHERE d.id = :dailyId", nativeQuery = true)
    Optional<BoardGetDetailDto> getDailyDetail (@Param("userId") Long userId, @Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM daily "
            + "  WHERE user_id = :userId"
            , nativeQuery = true)
    void deleteAllByUser(@Param("userId") Long userId);

    @Query(value = " SELECT new com.example.emotrak.dto.board.BoardImgRequestDto(d.id, d.imgUrl)"
            + "   FROM Daily d"
            + "  WHERE d.emotion.id in (:emo)"
            + "    AND d.share = true "
            + "  ORDER BY d.createdAt desc ")
    Page<BoardImgRequestDto> getBoardImagesRecent(@Param("emo") List<Long> emoList, Pageable pageable);

    @Query(value = " SELECT new com.example.emotrak.dto.board.BoardImgRequestDto(d.id, d.imgUrl)"
            + "   FROM Daily d left join Likes l on d.id = l.daily.id"
            + "  WHERE d.emotion.id in (:emo)"
            + "    AND d.share = true "
            + "  GROUP BY d.id, d.imgUrl"
            + "  ORDER BY count(l.daily.id)  desc, d.createdAt desc")
    Page<BoardImgRequestDto> getBoardImagesPopular(@Param("emo") List<Long> emoList, Pageable pageable);
}
