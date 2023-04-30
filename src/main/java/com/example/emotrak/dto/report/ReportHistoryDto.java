package com.example.emotrak.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportHistoryDto {
    private Long reportId;
    private Long id;
    private String nickname;
    private String email;
    private String reason;
    private Long count;
    public ReportHistoryDto(Object[] object) {
        this.reportId = ((BigInteger) object[1]).longValue();
        this.id = ((BigInteger) object[2]).longValue();
        this.nickname = (String) object[3];
        this.email = (String) object[4];
        this.reason = (String) object[5];
        this.count = ((BigInteger) object[6]).longValue();
    }
}
