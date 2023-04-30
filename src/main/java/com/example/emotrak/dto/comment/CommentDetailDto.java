package com.example.emotrak.dto.comment;

public interface CommentDetailDto {
    Long getId();
    String getComment();
    String getCreatedAt();
    boolean getHasAuth();
    String getNickname();
    int getLikesCnt();
    boolean getHasLike();
    boolean getHasReport(); // 댓글을 신고했는지 여부
}
