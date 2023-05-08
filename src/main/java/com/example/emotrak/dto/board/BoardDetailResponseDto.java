package com.example.emotrak.dto.board;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Daily;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto implements Serializable {
    private Long id;
    private String date;
    private int year;
    private int month;
    private int day;
    private Long emoId;
    private int star;
    private String detail;
    private String imgUrl;
    private boolean hasAuth;
    private String nickname;
    private int likesCnt;
    private boolean restrict;
    private boolean hasLike;
    private boolean lastPage; // 마지막 페이지 여부
    private boolean draw;
    private boolean hasReport; // 게시물을 신고했는지 여부
    private int totalComments; // 게시글의 전체 댓글 수
    @JsonProperty("comments")
    private List<CommentDetailResponseDto> commentDetailResponseDtoList;

    private String formatCreatedAt(LocalDateTime createdAt) {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public BoardDetailResponseDto(Daily daily, Long userId, long likesCnt, long userLikesCnt, long reportCnt, long totalComments) {
        this.id = daily.getId();
        this.date = formatCreatedAt(daily.getCreatedAt());
        this.year = daily.getDailyYear();
        this.month = daily.getDailyMonth();
        this.day = daily.getDailyDay();
        this.emoId = daily.getEmotion().getId();
        this.star = daily.getStar();
        this.detail = daily.getDetail();
        this.imgUrl = daily.getImgUrl();
        this.hasAuth = Objects.equals(daily.getUser().getId(), userId);
        this.nickname = daily.getUser().getNickname();
        this.likesCnt = (int) likesCnt;
        this.restrict = daily.isHasRestrict();
        this.hasLike = userLikesCnt > 0;
        this.hasReport = reportCnt > 0;
        this.totalComments = (int) totalComments;
    }

}
