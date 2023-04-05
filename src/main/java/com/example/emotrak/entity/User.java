package com.example.emotrak.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false)
    private String password;

    private Long kakaoId;

    private Long naverId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String password, String email,String nickname, UserRoleEnum role) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
    public User(String password, String email,String nickname,Long kakaoId, UserRoleEnum role) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.role = role;
    }
    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public boolean hasAdmin() {
        // 관리자 권한을 확인하는 로직을 작성합니다.
        return this.role == UserRoleEnum.ADMIN;
    }
}