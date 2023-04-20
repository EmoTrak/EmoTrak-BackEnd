package com.example.emotrak.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequestDto {
    @ApiModelProperty(value = "수정할 패스워드", required = true, example = "패스워드 예시")
    private String password;
}
