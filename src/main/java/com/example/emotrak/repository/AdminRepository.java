package com.example.emotrak.repository;

import com.example.emotrak.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<Report, Long> {

    @Query(value = "select a.daily_id, "
            + "        a.count, "
            + "        reason "
            + "   from report r,"
            + "        ("
            + "            select daily_id, count(*) as count from report r2"
            + "            group by daily_id"
            + "        ) a"
            + " where a.daily_id = r.daily_id", nativeQuery = true)
    List<Object[]> getReportBoard();

    @Query(value = "select r1.comment_id,"
            + "        a.count,"
            + "        r1.reason"
            + "   from report r1,"
            + "        ("
            + "        select r2.comment_id, count(*) as count from report r2"
            + "        group by r2.comment_id"
            + "        ) a"
            + " where a.comment_id = r1.comment_id",nativeQuery = true)
    List<Object[]> getReportComment();
}
