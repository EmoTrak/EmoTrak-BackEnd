package com.example.emotrak.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmailRequestDto {

    @ApiModelProperty(value = "체크할 이메일 주소", required = true, example = "이메일 예시")
    private String email;
}