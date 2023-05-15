package com.example.emotrak.dto.report;

public interface ReportQueryDto {
    Long getTotalCount();
    Long getReportId();
    Long getId();
    String getNickname();
    String getEmail();
    String getReason();
    Long getCount();

    void setTotalCount(Long totalCount);
    void setReportId(Long reportId);
    void setId(Long id);
    void setNickname(String nickname);
    void setEmail(String email);
    void setReason(String reason);
    void serCount(Long count);
}
