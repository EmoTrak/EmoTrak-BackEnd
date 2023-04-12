package com.example.emotrak.dto;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardDetailResponseDto {
    private Long id;
    private String date;
    private Long emoId;
    private int star;
    private String detail;
    private String imgUrl;
    private boolean hasAuth;
    private String nickname;
    private int likesCnt;
    private boolean restrict;
    private boolean hasLike;
    @JsonProperty("comments")
    private List<CommentDetailResponseDto> commentDetailResponseDtoList;

    private String formatCreatedAt(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.toLocalDate().equals(now.toLocalDate())) {
            // 오늘 작성한 게시글인 경우, 시간 정보까지 반환
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            // 오늘 이전에 작성한 게시글인 경우, 시간 정보는 09:00:00으로 고정하여 반환
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 09:00:00";
        }
    }

    public BoardDetailResponseDto(Daily daily, User user, List<CommentDetailResponseDto> commentDetailResponseDtoList, boolean hasLike) {
        this.id = daily.getId();
        this.date = formatCreatedAt(daily.getCreatedAt());
        this.emoId = daily.getEmotion().getId();
        this.star = daily.getStar();
        this.detail = daily.getDetail();
        this.imgUrl = daily.getImgUrl();
        if (user != null) this.hasAuth = daily.getUser().getId().equals(user.getId());
        this.commentDetailResponseDtoList = commentDetailResponseDtoList;
        this.nickname = daily.getUser().getNickname();
        this.likesCnt = daily.getBoardLikesCnt();
        this.restrict = daily.isHasRestrict();
        this.hasLike = hasLike;
    }
}
