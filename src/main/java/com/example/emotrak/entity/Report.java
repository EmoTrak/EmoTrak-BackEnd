package com.example.emotrak.entity;

import com.example.emotrak.dto.ReportRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Report extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "dailyId")
    private Daily daily;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;


    public Report(ReportRequestDto reportRequestDto, User user, Daily daily) {
        this.reason = reportRequestDto.getReason();
        this.user = user;
        this.daily = daily;
    }

    public Report(ReportRequestDto reportRequestDto, User user, Comment comment) {
        this.reason = reportRequestDto.getReason();
        this.user = user;
        this.comment = comment;
    }
}
