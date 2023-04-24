package com.example.emotrak.Service;

import com.example.emotrak.dto.graph.GraphResponseDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final GraphRepository graphRepository;

    @Transactional
    public List<GraphResponseDto> graph(int year, User user){
        Long userId = user.getId();
        List<Object[]> objectList = graphRepository.getGraph(year, userId);

        List<GraphResponseDto> graphResponseDtoList = new ArrayList<>();
        GraphResponseDto graphResponseDto = null;
        for (Object[] object : objectList) {
            int month = ((Integer)object[0]).intValue();
            Long id = ((BigInteger) object[1]).longValue();
            float count = ((BigDecimal) object[2]).floatValue();
            float percentage = ((BigDecimal) object[3]).floatValue();

            if (graphResponseDto == null || graphResponseDto.getMonth() != month) {
                graphResponseDto = new GraphResponseDto(object);
                graphResponseDtoList.add(graphResponseDto);
            }
            graphResponseDto.addidCountPercentage(id, count, percentage);
        }
        return graphResponseDtoList;
    }
}
