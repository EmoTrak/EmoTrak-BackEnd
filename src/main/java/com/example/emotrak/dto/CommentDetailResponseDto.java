package com.example.emotrak.dto;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDetailResponseDto {
    private Long id;
    private String comment;
    private String createdAt;
    private boolean hasAuth;
    private String nickname;
    private int likesCnt;
    private boolean hasLike;

    private String formatCreatedAt(LocalDateTime createdAt) {
       return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CommentDetailResponseDto(Comment comment, User user, boolean hasLike) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = formatCreatedAt(comment.getCreatedAt());
        if (user != null) this.hasAuth = comment.getUser().getId().equals(user.getId()) || user.hasAdmin();
        this.nickname = comment.getUser().getNickname();
        this.likesCnt = comment.getCmtLikesCnt();
        this.hasLike = hasLike;
    }
}
