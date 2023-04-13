package com.example.emotrak.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {

    private int year;
    private int month;
    private int day;
    private Long emoId;
    private int star;
    private String detail;
    private boolean share;
    private boolean deleteImg;

}
