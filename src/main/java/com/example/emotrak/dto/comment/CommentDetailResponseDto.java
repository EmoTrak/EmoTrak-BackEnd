package com.example.emotrak.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
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

    public CommentDetailResponseDto(Object[] object) {
        this.id = ((BigInteger)object[0]).longValue();
        this.comment = (String) object[1];
        this.createdAt = (String) object[2];
        this.hasAuth = ((BigInteger) object[3]).intValue() == 0 ? false : true;
        this.nickname = (String) object[4];
        this.likesCnt = ((BigInteger)object[5]).intValue();
        this.hasLike = ((BigInteger) object[6]).intValue() == 0 ? false : true;
        this.hasReport = ((BigInteger) object[7]).intValue() == 0 ? false : true;
    }
}
