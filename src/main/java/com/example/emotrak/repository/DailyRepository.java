package com.example.emotrak.repository;

import com.example.emotrak.dto.DailyDetailResponseDto;
import com.example.emotrak.dto.DailyMonthResponseDto;
import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyRepository extends JpaRepository<Daily, Long> {
    @Query(value = " select new com.example.emotrak.dto.DailyMonthResponseDto( d.id, d.day, d.emotion.id, d.detail )"
                 + "   from Daily d "
                 + "  where d.year = :year "
                 + "    and d.month = :month "
                 + "    and d.user.id = :userId "
                 + "  order by d.day, d.createdAt")
    List<DailyMonthResponseDto> getDailyMonth(@Param("year") int year
                                            , @Param("month") int month
                                            , @Param("userId") Long userId);

    @Query(value = "select new com.example.emotrak.dto.DailyDetailResponseDto( d.id, d.day, d.emotion.id, d.star, d.detail, d.imgUrl ) "
                 + "  from Daily d, Daily c "
                 + " where d.year = c.year "
                 + "   and d.month = c.month "
                 + "   and d.day = c.day "
                 + "   and d.user.id = c.user.id "
                 + "   and c.id = :dailyId")
    List<DailyDetailResponseDto> getDailyDetail(@Param("dailyId") Long dailyId);

    @Query(value = "SELECT d.emotion_id, d.month, COUNT(*) "
                 + "     , ROUND((sum(star) * 100/ (COUNT(*) * total.emotion_total)), 2) as percentage "
                 + "  FROM daily d, (SELECT d2.user_id, COUNT(*) as emotion_total "
                 + "                   FROM daily d2 "
                 + "                  WHERE d2.year = :year AND d2.user_id = :userId "
                 + "                  GROUP BY d2.user_id) total "
                 + " WHERE d.year = :year AND d.user_id = :userId "
                 + "   AND d.user_id = total.user_id "
                 + " GROUP BY d.emotion_id "
                 + " ORDER BY d.emotion_id", nativeQuery = true)
    List<Object[]> getDailyCount(@Param("year") int year, @Param("userId") Long userId);
}
