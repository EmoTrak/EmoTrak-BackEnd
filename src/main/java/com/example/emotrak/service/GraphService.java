package com.example.emotrak.service;

import com.example.emotrak.dto.graph.GraphPercentageDto;
import com.example.emotrak.dto.graph.GraphQueryDto;
import com.example.emotrak.dto.graph.GraphResponseDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final GraphRepository graphRepository;

    @Transactional
    public List<GraphResponseDto> graph(int year, User user) {
        Long userId = user.getId();

        List<GraphQueryDto> graphQueryDtoList = graphRepository.getGraph(year, userId);

        List<GraphResponseDto> graphResponseDtoList = new ArrayList<>();
        List<GraphPercentageDto> graphPercentageDtoList = new ArrayList<>();

        for (GraphQueryDto graphQueryDto : graphQueryDtoList) {
            graphPercentageDtoList.add(new GraphPercentageDto(graphQueryDto));
            if (graphQueryDto.getId() == 6) {
                graphResponseDtoList.add(new GraphResponseDto(graphQueryDto.getMonth(), graphPercentageDtoList));
                graphPercentageDtoList = new ArrayList<>();
            }
        }
        return graphResponseDtoList;
    }
}
