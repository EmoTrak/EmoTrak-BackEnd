package com.example.emotrak.dto.report;

public class ReportQueryDtoImpl implements ReportQueryDto{
    private Long totalCount;
    private Long reportId;
    private Long id;
    private String nickname;
    private String email;
    private String reason;
    private Long count;

    @Override
    public Long getTotalCount() {
        return totalCount;
    }

    @Override
    public Long getReportId() {
        return reportId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public Long getCount() {
        return count;
    }

    @Override
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void serCount(Long count) {
        this.count = count;
    }
}
