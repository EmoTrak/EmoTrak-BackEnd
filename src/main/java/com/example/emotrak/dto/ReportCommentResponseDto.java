package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportCommentResponseDto {
    private Long id;
    private Long count;
    private List<ReportBoardReasonResponseDto> reason = new ArrayList<>();

    public ReportCommentResponseDto(Object[] object){
        this.id = ((BigInteger) object[0]).longValue();
        this.count = ((BigInteger) object[1]).longValue();
    }

    public void addComment(String reason){
        this.reason.add(new ReportBoardReasonResponseDto(reason));
    }
}
