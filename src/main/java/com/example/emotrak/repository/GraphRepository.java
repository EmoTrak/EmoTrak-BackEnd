package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GraphRepository extends JpaRepository<Daily, Long> {
    @Query(value = " SELECT a.daily_month,"
                 + "        a.emotion_id,"
                 + "        ROUND(COALESCE(AVG(d.star), 0), 1)             AS count,"
                 + "        COALESCE(ROUND(a.cocount * 100 / a.sum, 2), 0) AS percentage"
                 + "   FROM (SELECT d.daily_year,"
                 + "                m.daily_month,"
                 + "                e.id                    AS emotion_id,"
                 + "                COALESCE(d.countsum, 0) AS sum,"
                 + "                COALESCE(d.cocount, 0)  AS cocount"
                 + "           FROM months m"
                 + "                CROSS JOIN emotion e"
                 + "                 LEFT JOIN (SELECT daily_year,"
                 + "                                   daily_month,"
                 + "                                   emotion_id,"
                 + "                                   COUNT(*)                                      AS cocount,"
                 + "                                   SUM(COUNT(*)) OVER (PARTITION BY daily_month) AS countsum"
                 + "                              FROM daily"
                 + "                             WHERE daily_year = :year"
                 + "                               AND user_id = :userId"
                 + "                             GROUP BY daily_year, daily_month, emotion_id) d"
                 + "                                ON m.daily_month = d.daily_month"
                 + "                               AND e.id = d.emotion_id"
                 + "                             ORDER BY m.daily_month, emotion_id"
                 + "                          ) a"
                 + "               LEFT JOIN daily d"
                 + "                 ON a.daily_year = d.daily_year"
                 + "                AND a.daily_month = d.daily_month"
                 + "                AND a.emotion_id = d.emotion_id"
                 + "                AND d.user_id = :userId"
                 + "  GROUP BY a.daily_month, a.emotion_id"
                 + "  ORDER BY a.daily_month, a.emotion_id", nativeQuery = true)
    List<Object[]> getGraph(@Param("year") int year,
                            @Param("userId") Long userId);
}
