package com.example.emotrak.dto.daily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyDetailResponseDto {
    private Long id;
    private int day;
    private Long emoId;
    private int star;
    private String detail;
    private String imgUrl;
    private boolean share;
    private boolean restrict;
    private boolean draw;

}
