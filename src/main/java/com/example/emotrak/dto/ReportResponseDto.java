package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class ReportResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private String reason;
    private Long count;

    public ReportResponseDto(Object[] object){
        this.id = ((BigInteger) object[0]).longValue();
        this.nickname = (String) object[1];
        this.email = (String) object[2];
        this.reason = (String) object[3];
        this.count = ((BigInteger) object[4]).longValue();
    }

}
