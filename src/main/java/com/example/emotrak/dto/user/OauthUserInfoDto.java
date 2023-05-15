package com.example.emotrak.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthUserInfoDto {
    private String id;
    private String email;
    private String nickname;

    public OauthUserInfoDto(String id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

}
