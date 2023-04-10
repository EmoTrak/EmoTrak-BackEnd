package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequestDto {

//    @NotBlank(message = "신고 사유를 입력해주세요.")
    private String reason;

}
