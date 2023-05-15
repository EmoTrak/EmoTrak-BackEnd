package com.example.emotrak.entity;

import com.example.emotrak.dto.comment.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Setter
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

    public Comment(CommentRequestDto commentRequestDto, Daily daily, User user) {
        this.comment = commentRequestDto.getComment();
        this.daily = daily;
        this.user = user;
    }

    public void updateComment(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }

}
