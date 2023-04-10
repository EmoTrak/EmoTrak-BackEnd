package com.example.emotrak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GraphResponseDto {
    private int month;
    private List<GraphIdCountResponseDto> graph;

    public GraphResponseDto(Object[] object) {
        this.month = ((Integer)object[0]).intValue();
        this.graph = new ArrayList<>();
    }

    public void addidCountPercentage(Long id, Long count, float percentage){
        this.graph.add(new GraphIdCountResponseDto(id,count,percentage));
    }
}
