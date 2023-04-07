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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyService {
    private final DailyRepository dailyRepository;

    @Transactional
    public DailyResponseDto<DailyMonthResponseDto> getDailyMonth(DailyRequestDto dailyRequestDto, User user) {
        List<DailyMonthResponseDto> dailyMonthResponseDtoList
                = dailyRepository.getDailyMonth(dailyRequestDto.getYear(), dailyRequestDto.getMonth(), user.getId());
        return new DailyResponseDto(dailyRequestDto.getYear(), dailyRequestDto.getMonth(), dailyMonthResponseDtoList);
    }

    @Transactional
    public DailyResponseDto getDailyDetail(Long dailyId) {
        Daily daily = getDaily(dailyId);
        List<DailyDetailResponseDto> dailyDetailResponseDtoList = dailyRepository.getDailyDetail(dailyId);
        return new DailyResponseDto(daily.getYear(), daily.getMonth(), dailyDetailResponseDtoList);
    }

    public Daily getDaily(Long dailyId) {
        return dailyRepository.findById(dailyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    public List<GraphResponseTestDto> getTest(User user) {
        int year = 2023;
        Long userId = user.getId();
        List<Object[]> objects = dailyRepository.getDailyCount(year, userId);

        List<GraphResponseTestDto> graphResponseTestDtoList = new ArrayList<>();
        for (Object[] object : objects) {
            GraphResponseTestDto graphResponseTestDto = new GraphResponseTestDto(object);
            graphResponseTestDtoList.add(graphResponseTestDto);
        }
        return graphResponseTestDtoList;
    }
}
