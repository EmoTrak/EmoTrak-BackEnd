package com.example.emotrak.entity;

import com.example.emotrak.dto.board.BoardRequestDto;
import lombok.*;
import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Daily extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int dailyYear;

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

    public Daily(Long id) {this.id = id;}

    //생성자
    public Daily(String imageUrl, BoardRequestDto boardRequestDto, User user, Emotion emotion) {
        this.imgUrl = imageUrl;
        this.user = user;
        this.dailyYear = boardRequestDto.getYear();
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
        this.dailyYear = boardRequestDto.getYear();
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
