package com.example.emotrak.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponseDto<T> {

    private int year;
    private int month;
    private T contents;

}
