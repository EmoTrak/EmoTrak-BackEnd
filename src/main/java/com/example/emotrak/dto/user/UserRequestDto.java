package com.example.emotrak.dto.user;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @ApiModelProperty(value = "체크할 이메일", required = true, example = "이메일 예시")
    private String email;
    @ApiModelProperty(value = "체크할 패스워드", required = true, example = "패스워드 예시")
    private String nickname;
}
