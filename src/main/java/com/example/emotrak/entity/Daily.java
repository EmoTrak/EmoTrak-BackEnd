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
    private int DailyYear;

    @Column(nullable = false)
    private int dailyMonth;

    @Column(nullable = false)
    private int dailyDay;

    @ManyToOne
    @JoinColumn(name = "emotionId")
    private Emotion emotion;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false, length = 3000)
    private String detail;

    @Column(nullable = false)
    private int star;

    @Column
    private String imgUrl;

    @Column(nullable = false)
    private boolean share;

    @Column(nullable = false)
    private boolean hasRestrict;

    @Column(nullable = false)
    private boolean draw;

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Daily(Long id) {
        this.id = id;
    }

    //생성자
    public Daily(String imageUrl, BoardRequestDto boardRequestDto, User user, Emotion emotion) {
        this.imgUrl = imageUrl;
        this.user = user;
        this.DailyYear = boardRequestDto.getYear();
        this.dailyMonth = boardRequestDto.getMonth();
        this.dailyDay = boardRequestDto.getDay();
        this.emotion = emotion;
        this.star = boardRequestDto.getStar();
        this.detail = boardRequestDto.getDetail();
        this.share = boardRequestDto.isShare();
        this.draw = boardRequestDto.isDraw();
    }

    //board 업데이트 메서드
    public void update(String newImageUrl, BoardRequestDto boardRequestDto, Emotion emotion) {
        this.imgUrl = newImageUrl;
        this.DailyYear = boardRequestDto.getYear();
        this.dailyMonth = boardRequestDto.getMonth();
        this.dailyDay = boardRequestDto.getDay();
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
