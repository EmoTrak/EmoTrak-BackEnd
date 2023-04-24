package com.example.emotrak.dto.board;

import com.example.emotrak.dto.comment.CommentDetailResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public BoardDetailResponseDto(Object[] daily, List<CommentDetailResponseDto> commentDetailResponseDtoList, Boolean lastPage) {
        this.id = ((BigInteger) daily[2]).longValue();
        this.date = (String) daily[3];
        this.emoId = ((BigInteger) daily[4]).longValue();
        this.star = (int) daily[5];
        this.detail = (String) daily[6];
        this.imgUrl = (String) daily[7];
        this.hasAuth = ((BigInteger) daily[8]).intValue() == 0 ? false : true;
        this.commentDetailResponseDtoList = commentDetailResponseDtoList;
        this.nickname = (String) daily[9];
        this.likesCnt =  ((BigInteger) daily[10]).intValue();
        this.restrict = (boolean) daily[11];
        this.hasLike = ((BigInteger) daily[12]).intValue() == 0 ? false : true;
        this.lastPage = lastPage;
        this.draw = (boolean) daily[13];
        this.hasReport = ((BigInteger) daily[14]).intValue() == 0 ? false : true;
        this.totalComments = ((BigInteger) daily[15]).intValue();
    }
}
