package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NaverUserInfoDto {
    private String id; // 변경
    private String email;
    private String nickname;

    public NaverUserInfoDto(String id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
