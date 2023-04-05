package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DailyMonthResponseDto {

    private Long id;
    private int day;
    private Long emoId;
    private String detail;

    public DailyMonthResponseDto(Long id, int day, Long emoId, String detail) {
        this.id = id;
        this.day = day;
        this.emoId = emoId;
        this.detail = detail;
    }
}
