package com.example.emotrak.dto.board;

import com.example.emotrak.dto.comment.CommentDetailDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

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
    private List<CommentDetailDto> commentDetailDtoList;

    public BoardDetailResponseDto(BoardGetDetailDto boardGetDetailDto, Page<CommentDetailDto> commentDetailDtoPage) {
        this.id = boardGetDetailDto.getDailyId();
        this.date = boardGetDetailDto.getCreatedAt();
        this.emoId = boardGetDetailDto.getEmotionId();
        this.star = boardGetDetailDto.getStar();
        this.detail = boardGetDetailDto.getDetail();
        this.imgUrl = boardGetDetailDto.getImgUrl();
        this.hasAuth = boardGetDetailDto.getAuth();
        this.commentDetailDtoList = commentDetailDtoPage.getContent();
        this.nickname = boardGetDetailDto.getNickname();
        this.likesCnt =  boardGetDetailDto.getLikeCount();
        this.restrict = boardGetDetailDto.getHasRestrict();
        this.hasLike = boardGetDetailDto.getHasLike();
        this.lastPage = !commentDetailDtoPage.hasNext();
        this.draw = boardGetDetailDto.getDraw();
        this.hasReport = boardGetDetailDto.getHasReport();
        this.totalComments = boardGetDetailDto.getCommentCount();
    }

}
