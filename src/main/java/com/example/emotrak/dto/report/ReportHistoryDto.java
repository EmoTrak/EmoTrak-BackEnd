package com.example.emotrak.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public ReportHistoryDto(ReportQueryDto reportQueryDto) {
        this.reportId = reportQueryDto.getReportId();
        this.id = reportQueryDto.getId();
        this.nickname = reportQueryDto.getNickname();
        this.email = reportQueryDto.getEmail();
        this.reason = reportQueryDto.getReason();
        this.count = reportQueryDto.getCount();
    }
}
