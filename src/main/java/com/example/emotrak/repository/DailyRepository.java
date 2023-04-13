package com.example.emotrak.repository;

import com.example.emotrak.dto.DailyDetailResponseDto;
import com.example.emotrak.dto.DailyMonthResponseDto;
import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyRepository extends JpaRepository<Daily, Long> {
    @Query(value = " select new com.example.emotrak.dto.DailyMonthResponseDto( d.id, d.dailyDay, d.emotion.id, d.detail )"
                 + "   from Daily d "
                 + "  where d.DailyYear = :year "
                 + "    and d.dailyMonth = :month "
                 + "    and d.user.id = :userId "
                 + "  order by d.dailyDay, d.createdAt")
    List<DailyMonthResponseDto> getDailyMonth(@Param("year") int year
                                            , @Param("month") int month
                                            , @Param("userId") Long userId);

    @Query(value = "select new com.example.emotrak.dto.DailyDetailResponseDto( d.id, d.dailyDay, d.emotion.id, d.star, d.detail, d.imgUrl, d.share, d.hasRestrict ) "
                 + "  from Daily d, Daily c "
                 + " where d.DailyYear = c.DailyYear "
                 + "   and d.dailyMonth = c.dailyMonth "
                 + "   and d.dailyDay = c.dailyDay "
                 + "   and d.user.id = c.user.id "
                 + "   and c.id = :dailyId")
    List<DailyDetailResponseDto> getDailyDetail(@Param("dailyId") Long dailyId);

}
