package com.example.emotrak.util;

import com.example.emotrak.entity.Timestamped;
import com.example.emotrak.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class RefreshToken extends Timestamped {

    @Id
    @Column(nullable = false)
    private Long id;

    @JoinColumn(name = "users_id",nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "token_value", nullable = false)
    private String value;
    @Column
    private Date expirationDate;

}