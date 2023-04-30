package com.example.emotrak.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {
    @ApiModelProperty(value = "가입할 이메일", required = true, example = "이메일 예시")
    private String email;
    @ApiModelProperty(value = "가입할 패스워드", required = true, example = "패스워드 예시")
    private String password;
    @ApiModelProperty(value = "가입할 닉네임", required = true, example = "닉네임 예시")
    private String nickname;

    //private boolean admin = false;

    //private String adminToken ="";
}
