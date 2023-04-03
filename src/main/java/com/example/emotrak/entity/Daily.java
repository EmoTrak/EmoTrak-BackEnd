package com.example.emotrak.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Daily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @ManyToOne
    @JoinColumn(name = "emotionId")
    private Emotion emotion;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private int star;

    @Column
    private String imgUrl;

    @Column(nullable = false)
    private boolean share;

}
