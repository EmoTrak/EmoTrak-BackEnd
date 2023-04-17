package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Daily, Long> {

    // 사용자가 오늘 작성한 게시물 수 검색
    @Query("SELECT COUNT(d) FROM Daily d WHERE d.user = :user AND DATE(d.createdAt) = CURRENT_DATE")
    long countDailyPostsByUserToday(@Param("user") User user);

    void deleteAllByUser(User user);

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
