package com.example.emotrak.dto.comment;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetailResponseDto {
    private Long id;
    private String comment;
    private String createdAt;
    private boolean hasAuth;
    private String nickname;
    private int likesCnt;
    private boolean hasLike;
    private boolean hasReport; // 댓글을 신고했는지 여부

    private String formatCreatedAt(LocalDateTime createdAt) {
       return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CommentDetailResponseDto(Comment comment, User user, int likesCnt, boolean hasLike,  boolean hasReport) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = formatCreatedAt(comment.getCreatedAt());
        this.hasAuth = (user != null) && (comment.getUser().getId().equals(user.getId()) || user.hasAdmin());
        this.nickname = comment.getUser().getNickname();
        this.likesCnt = likesCnt;
        this.hasLike = hasLike;
        this.hasReport = hasReport;
    }
}
