package com.example.emotrak.dto.board;

public class BoardGetDetailDtoImpl implements BoardGetDetailDto {
    private boolean share;
    private Long userId;
    private Long dailyId;
    private String createAt;
    private int year;
    private int month;
    private int day;
    private Long emotionId;
    private int star;
    private String detail;
    private String imgUrl;
    private boolean auth;
    private String nickname;
    private int likeCount;
    private boolean hasRestrict;
    private boolean hasLike;
    private boolean draw;
    private boolean hasReport;
    private int commentCount;

    @Override
    public boolean getShare() {
        return share;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public Long getDailyId() {
        return dailyId;
    }

    @Override
    public String getCreatedAt() {
        return createAt;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public Long getEmotionId() {
        return emotionId;
    }

    @Override
    public int getStar() {
        return star;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public boolean getAuth() {
        return auth;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public int getLikeCount() {
        return likeCount;
    }

    @Override
    public boolean getHasRestrict() {
        return hasRestrict;
    }

    @Override
    public boolean getHasLike() {
        return hasLike;
    }

    @Override
    public boolean getDraw() {
        return draw;
    }

    @Override
    public boolean getHasReport() {
        return hasReport;
    }

    @Override
    public int getCommentCount() {
        return commentCount;
    }

    @Override
    public void setShare(boolean share) {
        this.share = share;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public void setDailyId(Long dailyId) {
        this.dailyId = dailyId;
    }

    @Override
    public void setCreatedAt(String createdAt) {
        this.createAt = createdAt;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public void setEmotionId(Long emotionId) {
        this.emotionId = emotionId;
    }

    @Override
    public void setStar(int star) {
        this.star = star;
    }

    @Override
    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public void setHasRestrict(boolean hasRestrict) {
        this.hasRestrict = hasRestrict;
    }

    @Override
    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    @Override
    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    @Override
    public void setHasReport(boolean hasReport) {
        this.hasReport = hasReport;
    }

    @Override
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
