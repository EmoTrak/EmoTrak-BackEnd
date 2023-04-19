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
public class NicknameRequestDto {
    @ApiModelProperty(value = "체크할 닉네임", required = true, example = "닉네임 예시")
    String nickname;
}
