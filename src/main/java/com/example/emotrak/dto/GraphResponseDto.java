package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
public class GraphResponseDto {
    private int month;
    private Long id;
    private Long count;
    private float percentage;

    public GraphResponseDto(Object[] object) {
        this.month = ((Integer)object[0]).intValue();
        this.id = ((BigInteger) object[1]).longValue();
        this.count = ((BigInteger) object[2]).longValue();
        this.percentage = ((BigDecimal) object[3]).floatValue();
    }
}
