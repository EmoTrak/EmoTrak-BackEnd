package com.example.emotrak.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$",message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "패스워드를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+]{8,15}$", message = "숫자와 영어 소문자와 특수문자를 사용해 8-15자리 비밀번호를 입력해주세요.")
    private String password;

    private boolean admin = false;

    private String adminToken ="";
}
