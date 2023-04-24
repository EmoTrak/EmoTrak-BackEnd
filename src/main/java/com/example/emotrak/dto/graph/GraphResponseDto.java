package com.example.emotrak.dto.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphResponseDto {
    private int month;
    private List<GraphIdCountResponseDto> graph;

    public GraphResponseDto(Object[] object) {
        this.month = ((Integer)object[0]).intValue();
        this.graph = new ArrayList<>();
    }

    public void addidCountPercentage(Long id, float count, float percentage){
        this.graph.add(new GraphIdCountResponseDto(id,count,percentage));
    }
}
