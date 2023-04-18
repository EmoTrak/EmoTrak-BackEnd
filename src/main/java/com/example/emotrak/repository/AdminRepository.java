package com.example.emotrak.repository;

import com.example.emotrak.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<Report, Long> {

    @Query(value = " SELECT c.totalCount,"
                +  "        r.id AS reportId,"
                +  "        a.daily_id AS id,"
                +  "        u.nickname,"
                +  "        u.email,"
                +  "        r.reason,"
                +  "        a.count"
                +  " FROM report r,"
                +  "      ("
                +  "          SELECT daily_id, count(*) AS count FROM report"
                +  "          GROUP BY  daily_id"
                +  "      ) a,"
                +  "      users u, "
                +  "      ("
                +  "          SELECT COUNT(*) AS totalCount FROM report"
                +  "          WHERE comment_id IS NULL"
                +  "      ) c"
                +  " WHERE a.daily_id = r.daily_id AND r.user_id = u.id"
                +  " ORDER BY count DESC ", nativeQuery = true)
    List<Object[]> getReportBoard(Pageable pageable);

    @Query(value = "SELECT c.totalCount,"
            + "       r.id AS reportId,"
            + "       a.daily_id AS id,"
            + "       u.nickname,"
            + "       u.email,"
            + "       r.reason,"
            + "       a.count"
            + " FROM report r,"
            + "      ("
            + "          SELECT daily_id, count(*) AS count From report"
            + "          GROUP BY daily_id"
            + "      ) a,"
            + "       users u,"
            + "      ("
            + "          SELECT COUNT(*) AS totalCount FROM report"
            + "          WHERE daily_id IS NULL"
            + "      ) c"
            + " WHERE a.daily_id = r.daily_id AND r.user_id = u.id"
            + " ORDER BY count DESC ", nativeQuery = true)
    List<Object[]> getReportComment(Pageable pageable);
}
