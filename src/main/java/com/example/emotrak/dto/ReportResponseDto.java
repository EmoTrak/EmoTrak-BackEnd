package com.example.emotrak.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long reportId;
    private Long id;
    private String nickname;
    private String email;
    private String reason;
    private Long count;

    public ReportResponseDto(Object[] object){
        this.reportId = ((BigInteger) object[0]).longValue();
        this.id = ((BigInteger) object[1]).longValue();
        this.nickname = (String) object[2];
        this.email = (String) object[3];
        this.reason = (String) object[4];
        this.count = ((BigInteger) object[5]).longValue();
    }

}
