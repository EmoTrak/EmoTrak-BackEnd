package com.example.emotrak.dto.board;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean lastPage; // 마지막 페이지 여부
    private boolean draw;
    private boolean hasReport; // 게시물을 신고했는지 여부
    private int totalComments; // 게시글의 전체 댓글 수
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

    public BoardDetailResponseDto(Daily daily, User user, Long ignored,
                                  long likesCnt, boolean hasLike, boolean lastPage, boolean hasReport, long totalComments) {
        this.id = daily.getId();
        this.date = formatCreatedAt(daily.getCreatedAt());
        this.emoId = daily.getEmotion().getId();
        this.star = daily.getStar();
        this.detail = daily.getDetail();
        this.imgUrl = daily.getImgUrl();
        this.hasAuth = (user != null) && daily.getUser().getId().equals(user.getId());
        this.commentDetailResponseDtoList = Collections.emptyList(); // 기본값으로 빈 리스트를 설정합니다.
        this.nickname = daily.getUser().getNickname();
        this.likesCnt = (int) likesCnt;
        this.restrict = daily.isHasRestrict();
        this.hasLike = hasLike;
        this.lastPage = lastPage;
        this.draw = daily.isDraw();
        this.hasReport = hasReport;
        this.totalComments = (int) totalComments;
    }

}
