package com.example.emotrak.repository;

import com.example.emotrak.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<Report, Long> {

    @Query(value = "select a.daily_id,"
            + "        b.nickname,"
            + "        b.email,"
            + "        reason,"
            + "        a.count"
            + " from report r,"
            + "      ("
            + "          select user_id, daily_id, count(*) as count from report r2"
            + "          group by daily_id"
            + "      ) a,"
            + "     ("
            + "         select * from users"
            + "      ) b"
            + " where a.daily_id = r.daily_id and r.user_id = b.id"
            + " order by count desc", nativeQuery = true)
    List<Object[]> getReportBoard();

    @Query(value = "select a.comment_id,"
            + "        b.nickname,"
            + "        b.email,"
            + "        reason,"
            + "        a.count"
            + " from report r,"
            + "      ("
            + "          select user_id, comment_id, count(*) as count from report r2"
            + "          group by comment_id"
            + "      ) a,"
            + "      ("
            + "          select * from users"
            + "      ) b"
            + " where a.comment_id = r.comment_id and r.user_id = b.id"
            + " order by count desc",nativeQuery = true)
    List<Object[]> getReportComment();
}
