package com.example.emotrak.dto.daily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMonthResponseDto {

    private Long id;
    private int day;
    private Long emoId;
    private String detail;

}
