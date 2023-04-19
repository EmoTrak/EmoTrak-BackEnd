package com.example.emotrak.dto.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

    @ApiModelProperty(value = "신고 사유", required = true, example = "저는 신고 사유 입니다")
    private String reason;

}
