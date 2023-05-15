package com.example.emotrak.dto.board;

public interface BoardGetDetailDto {
    boolean getShare();
    Long getUserId();
    Long getDailyId();
    String getCreatedAt();
    int getYear();
    int getMonth();
    int getDay();
    Long getEmotionId();
    int getStar();
    String getDetail();
    String getImgUrl();
    boolean getAuth();
    String getNickname();
    int getLikeCount();
    boolean getHasRestrict();
    boolean getHasLike();
    boolean getDraw();
    boolean getHasReport();
    int getCommentCount();

    void setShare(boolean share);
    void setUserId(Long userId);
    void setDailyId(Long dailyId);
    void setCreatedAt(String createdAt);
    void setYear(int year);
    void setMonth(int month);
    void setDay(int day);
    void setEmotionId(Long emotionId);
    void setStar(int star);
    void setDetail(String detail);
    void setImgUrl(String imgUrl);
    void setAuth(boolean auth);
    void setNickname(String nickname);
    void setLikeCount(int likeCount);
    void setHasRestrict(boolean hasRestrict);
    void setHasLike(boolean hasLike);
    void setDraw(boolean draw);
    void setHasReport(boolean hasReport);
    void setCommentCount(int commentCount);
}