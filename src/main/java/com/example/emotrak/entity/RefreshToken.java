package com.example.emotrak.entity;

import lombok.*;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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