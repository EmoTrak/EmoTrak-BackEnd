package com.example.emotrak.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "감정글 요청 정보")
public class BoardRequestDto {

    @ApiModelProperty(value = "그림일기 여부", required = true, example = "true")
    private boolean draw;

    @ApiModelProperty(value = "년도", required = true, example = "2022")
    private int year;

    @ApiModelProperty(value = "월", required = true, example = "4")
    private int month;

    @ApiModelProperty(value = "일", required = true, example = "4")
    private int day;

    @ApiModelProperty(value = "Emotion ID", required = true, example = "1")
    private Long emoId;

    @ApiModelProperty(value = "별점", required = true, example = "5")
    private int star;

    @ApiModelProperty(value = "상세 내용", required = true, example = "오늘은 기분이 좋았어요!")
    private String detail;

    @ApiModelProperty(value = "공유 여부", required = true, example = "true")
    private boolean share;

    @ApiModelProperty(value = "이미지 삭제 여부", required = true, example = "true")
    private boolean deleteImg;

}
