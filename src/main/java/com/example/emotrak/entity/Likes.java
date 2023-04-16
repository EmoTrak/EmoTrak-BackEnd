package com.example.emotrak.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@ToString
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dailyId")
    private Daily daily;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;


    public Likes(Daily daily, User user) {
        this.daily = daily;
        this.user = user;
    }

   public Likes(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
   }

}
