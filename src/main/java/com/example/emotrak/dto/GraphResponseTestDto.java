package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class GraphResponseTestDto {
    private Long id;
    private int month;
    private Long count;
    private float avg;

    public GraphResponseTestDto(Object[] object) {
        this.id = ((BigInteger) object[0]).longValue();
        this.month = ((Integer)object[1]).intValue();
        this.count = ((BigInteger) object[2]).longValue();
        this.avg = ((BigDecimal) object[3]).floatValue();
    }
}
