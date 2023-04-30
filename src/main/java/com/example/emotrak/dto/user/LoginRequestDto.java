package com.example.emotrak.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    //@NotBlank(message = "이메일을 입력해주세요.")
    private String email;
    //@NotBlank(message = "패스워드를 입력해주세요.")
    private String password;
}