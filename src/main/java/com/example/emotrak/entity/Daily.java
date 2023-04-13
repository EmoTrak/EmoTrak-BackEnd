package com.example.emotrak.entity;

import com.example.emotrak.dto.BoardRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Daily extends Timestamped {

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

    @Column(nullable = false)
    private boolean hasRestrict;

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Comment> comments;

    //생성자
    public Daily(String imageUrl, BoardRequestDto boardRequestDto, User user, Emotion emotion) {
        this.imgUrl = imageUrl;
        this.user = user;
        this.year = boardRequestDto.getYear();
        this.month = boardRequestDto.getMonth();
        this.day = boardRequestDto.getDay();
        this.emotion = emotion;
        this.star = boardRequestDto.getStar();
        this.detail = boardRequestDto.getDetail();
        this.share = boardRequestDto.isShare();
    }

    //board 업데이트 메서드
    public void update(String newImageUrl, BoardRequestDto boardRequestDto, Emotion emotion) {
        this.imgUrl = newImageUrl;
        this.year = boardRequestDto.getYear();
        this.month = boardRequestDto.getMonth();
        this.day = boardRequestDto.getDay();
        this.emotion = emotion;
        this.star = boardRequestDto.getStar();
        this.detail = boardRequestDto.getDetail();
        this.share = boardRequestDto.isShare();
    }

    public void restricted(){
        this.share = false;
        this.hasRestrict = true;
    }

}
