package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportBoardReasonResponseDto {
    private String reason;

    public ReportBoardReasonResponseDto(String reason){
        this.reason = reason;
    }
}
