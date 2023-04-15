package com.example.emotrak.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    //@NotBlank(message = "이메일을 입력해주세요.")
    private String email;
    //@NotBlank(message = "패스워드를 입력해주세요.")
    private String password;
}