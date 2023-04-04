package com.example.emotrak.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BoardRequestDto {

    private int year;
    private int month;
    private int day;
    private Long emoId; //Api 확인 필요
    private int star;
    private String detail;
    private boolean share;

}
