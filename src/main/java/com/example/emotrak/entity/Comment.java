package com.example.emotrak.entity;

import com.example.emotrak.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dailyId")
    private Daily daily;

    @Column(nullable = false)
    private int cmtLikesCnt;

    public Comment(CommentRequestDto commentRequestDto, Daily daily, User user) {
        this.comment = commentRequestDto.getComment();
        this.daily = daily;
        this.user = user;
        this.cmtLikesCnt = 0;
    }

    public void updateComment(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }

    //
    public void plusLikesCount() {
        this.cmtLikesCnt++;
    }

    public void minusLikesCount() {
        this.cmtLikesCnt--;
    }


}
