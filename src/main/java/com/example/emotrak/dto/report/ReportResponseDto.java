package com.example.emotrak.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportResponseDto {
    private long totalCount;
    private List<ReportHistoryDto> contents = new ArrayList<>();

    public ReportResponseDto(long totalCount, List<ReportHistoryDto> reportHistoryDtoList) {
        this.totalCount = totalCount;
        this.contents = reportHistoryDtoList;
    }
}
