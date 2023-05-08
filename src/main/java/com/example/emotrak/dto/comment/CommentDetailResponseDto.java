package com.example.emotrak.dto.comment;

import com.example.emotrak.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetailResponseDto implements Serializable {
    private Long id;
    private String comment;
    private String createdAt;
    private boolean hasAuth; // 로그인을 안 했을 경우에는 false, 로그인을 했을 경우 해당 글의 작성자와 로그인한 사용자가 같은 경우에는 true
    private String nickname;
    private int likesCnt;
    private boolean hasLike;
    private boolean hasReport; // 댓글을 신고했는지 여부

    private String formatCreatedAt(LocalDateTime createdAt) {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public CommentDetailResponseDto(Comment comment, Long userId, long likesCnt, long userLikesCnt, long reportCnt) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = formatCreatedAt(comment.getCreatedAt());
//        this.hasAuth = userId != null && comment.getUser().getId().equals(userId);
        this.hasAuth = Objects.equals(comment.getUser().getId(), userId);
        this.nickname = comment.getUser().getNickname();
        this.likesCnt = (int) likesCnt;
        this.hasLike = userLikesCnt > 0;
        this.hasReport = reportCnt > 0;
    }

}