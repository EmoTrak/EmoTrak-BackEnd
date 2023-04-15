package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GraphRepository extends JpaRepository<Daily, Long> {
    @Query(value = " SELECT a.month"
                 + "      , a.emotion_id"
                 + "      , CASE WHEN (COALESCE(avg(d.star), 0) = 0) THEN 0 ELSE count(*) END count "
                 + "      , COALESCE( ROUND( AVG(d.star) * 100 / a.sum, 2), 0) percentage"
                 + " FROM ("
                 + "          SELECT m.month"
                 + "               , e.id as emotion_id"
                 + "               , COALESCE(d.starsum, 0) as sum"
                 + "          FROM months m"
                 + "                   CROSS JOIN emotion e"
                 + "                   LEFT JOIN ("
                 + "              select daily_month"
                 + "                   , SUM(a.staravg) starsum"
                 + "                from ( select d.daily_month"
                 + "                          , AVG(star) staravg"
                 + "                      from daily d"
                 + "                     where d.daily_year = :year"
                 + "                       and user_id = :userId"
                 + "                     group by daily_year, daily_month, emotion_id"
                 + "                   ) a"
                 + "               group by daily_month"
                 + "          ) d ON m.month = d.daily_month"
                 + "          ORDER BY m.month"
                 + "      ) a LEFT JOIN daily d"
                 + "                    ON a.month = d.daily_month"
                 + "                        AND a.emotion_id = d.emotion_id"
                 + "                        AND d.user_id = :userId"
                 + " GROUP BY a.month, a.emotion_id "
                 + " ORDER BY a.month, a.emotion_id;", nativeQuery = true)
    List<Object[]> getGraph(@Param("year") int year,
                            @Param("userId") Long userId);
}
