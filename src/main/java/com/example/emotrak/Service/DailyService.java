package com.example.emotrak.Service;

import com.example.emotrak.dto.DailyMonthResponseDto;
import com.example.emotrak.dto.DailyRequestDto;
import com.example.emotrak.dto.DailyResponseDto;
import com.example.emotrak.entity.User;
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
    public DailyResponseDto<DailyMonthResponseDto> getDailyMonth(DailyRequestDto dailyRequestDto, User user) {
        List<DailyMonthResponseDto> dailyMonthResponseDtoList = dailyRepository.getDailyMonth(dailyRequestDto.getYear(), dailyRequestDto.getMonth(), user.getId());
        return new DailyResponseDto(dailyRequestDto.getYear(), dailyRequestDto.getMonth(), dailyMonthResponseDtoList);
    }
}
