package com.example.emotrak.Service;

import com.example.emotrak.dto.*;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.DailyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyService {
    private final DailyRepository dailyRepository;

    @Transactional
    public DailyResponseDto<DailyMonthResponseDto> getDailyMonth(int year, int month, User user) {
        List<DailyMonthResponseDto> dailyMonthResponseDtoList
                = dailyRepository.getDailyMonth(year, month, user.getId());
        return new DailyResponseDto(year, month, dailyMonthResponseDtoList);
    }

    @Transactional
    public DailyResponseDto getDailyDetail(Long dailyId, User user) {
        Daily daily = getDaily(dailyId);
        if (!daily.getUser().getId().equals(user.getId())) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<DailyDetailResponseDto> dailyDetailResponseDtoList = dailyRepository.getDailyDetail(dailyId);
        return new DailyResponseDto(daily.getYear(), daily.getMonth(), dailyDetailResponseDtoList);
    }

    public Daily getDaily(Long dailyId) {
        return dailyRepository.findById(dailyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

}
