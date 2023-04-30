package com.example.emotrak.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @ApiModelProperty(value = "댓글 내용", required = true, example = "저는 댓글 예시입니다.")
    private String comment;


}
