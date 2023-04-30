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
}