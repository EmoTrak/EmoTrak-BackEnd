package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
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

    @Query(value = " SELECT d.share, d.user_id, d.id ,DATE_FORMAT(d.created_at, '%Y-%m-%d %H:%i:%s') AS created_at"
            + "      , d.emotion_id, d.star, d.detail, d.img_url "
            + "      , CASE WHEN d.user_id = :userId THEN true ELSE false END auth "
            + "      , u.nickname, l.count, d.has_restrict, COALESCE(l2.has_like, false) has_like"
            + "      , d.draw, COALESCE(r.has_report, false) has_report, c.count coment_count  "
            + "   FROM daily d "
            + "        JOIN users u ON d.user_id = u.id "
            + "        JOIN (SELECT count(*) count FROM likes WHERE daily_id = :dailyId) l "
            + "        JOIN (SELECT count(*) count FROM comment WHERE daily_id = :dailyId) c "
            + "   LEFT JOIN (SELECT true has_like FROM likes WHERE daily_id = :dailyId AND user_id = :userId) l2 "
            + "              ON l2.has_like IS NOT NULL "
            + "   LEFT JOIN (SELECT true has_report FROM report WHERE daily_id = :dailyId AND user_id = :userId) r "
            + "              ON r.has_report IS NOT NULL "
            + "  WHERE d.id = :dailyId", nativeQuery = true)
    List<Object[]> getDailyDetail (@Param("userId") Long userId, @Param("dailyId") Long dailyId);

    @Modifying
    @Query(value = " DELETE FROM daily "
            + "  WHERE user_id = :userId"
            , nativeQuery = true)
    void deleteAllByUser(@Param("userId") Long userId);

    @Query(value = " SELECT d.id, d.img_url "
            + "   FROM daily d"
            + "  WHERE d.emotion_id in (:emo)"
            + "    AND d.share = true "
            + "  ORDER BY d.created_at desc ", nativeQuery = true)
    List<Object[]> getBoardImagesRecent(@Param("emo") List<Long> emoList, Pageable pageable);

    @Query(value = " SELECT d.id, d.img_url"
            + "   FROM daily d left join likes l on d.id = l.daily_id"
            + "  WHERE d.emotion_id in (:emo)"
            + "    AND d.share = true "
            + "  GROUP BY d.id, d.img_url"
            + "  ORDER BY count(l.daily_id)  desc, d.created_at desc", nativeQuery = true)
    List<Object[]> getBoardImagesPopular(@Param("emo") List<Long> emoList, Pageable pageable);
}
