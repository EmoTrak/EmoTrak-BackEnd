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
    private String email;
    private String comment;
    private String createdAt;
    private boolean hasAuth;
    private String nickname;
    private int cmtLikesCnt;

    private String formatCreatedAt(LocalDateTime createdAt) {
       return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CommentDetailResponseDto(Comment comment, User user) {
        this.id = comment.getId();
        this.email = comment.getUser().getEmail();
        this.comment = comment.getComment();
        this.createdAt = formatCreatedAt(comment.getCreatedAt());
        this.hasAuth = comment.getUser().equals(user) || user.hasAdmin();
        this.nickname = user.getNickname();
        this.cmtLikesCnt = comment.getCmtLikesCnt();
    }
}
