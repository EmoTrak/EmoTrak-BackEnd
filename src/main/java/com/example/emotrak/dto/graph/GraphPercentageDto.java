package com.example.emotrak.dto.graph;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GraphPercentageDto {
    private Long id;
    private float count;
    private float percentage;

    public GraphPercentageDto(GraphQueryDto graphQueryDto) {
        this.id = graphQueryDto.getId();
        this.count = graphQueryDto.getCount();
        this.percentage = graphQueryDto.getPercentage();
    }
}
