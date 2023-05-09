package com.example.emotrak.dto.daily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponseDto<T> implements Serializable {

    private int year;
    private int month;
    private T contents;

}
