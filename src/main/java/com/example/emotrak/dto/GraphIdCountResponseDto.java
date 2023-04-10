package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GraphIdCountResponseDto {
    private Long id;
    private Long count;
    private float percentage;

    public GraphIdCountResponseDto(Long id, Long count, float percentage){
        this.id = id;
        this.count = count;
        this.percentage = percentage;
    }
}
