package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GraphRepository extends JpaRepository<Daily, Long> {
    @Query(value = " SELECT a.daily_month"
                 + "      , a.emotion_id"
                 + "      , IF (COALESCE(avg(d.star), 0) = 0, 0, count(*)) count"
                 + "      , COALESCE( ROUND( AVG(d.star) * 100 / a.sum, 2), 0) percentage"
                 + " FROM ("
                 + "          SELECT m.daily_month"
                 + "               , e.id as emotion_id"
                 + "               , COALESCE(d.starsum, 0) as sum"
                 + "          FROM months m"
                 + "                   CROSS JOIN emotion e"
                 + "                   LEFT JOIN ("
                 + "              select month"
                 + "                   , SUM(a.staravg) starsum"
                 + "                from ( select d.daily_month"
                 + "                          , AVG(star) staravg"
                 + "                      from daily d"
                 + "                     where d.year = :year"
                 + "                       and user_id = :userId"
                 + "                     group by year, daily_month, emotion_id"
                 + "                   ) a"
                 + "               group by daily_month"
                 + "          ) d ON m.daily_month = d.daily_month"
                 + "          ORDER BY m.daily_month"
                 + "      ) a LEFT JOIN daily d"
                 + "                    ON a.daily_month = d.daily_month"
                 + "                        AND a.emotion_id = d.emotion_id"
                 + "                        AND d.user_id = :userId"
                 + " GROUP BY a.daily_month, a.emotion_id "
                 + " ORDER BY a.daily_month, a.emotion_id;", nativeQuery = true)
    List<Object[]> getGraph(@Param("year") int year,
                            @Param("userId") Long userId);
}
