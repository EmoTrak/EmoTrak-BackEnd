package com.example.emotrak.Service;

import com.example.emotrak.dto.GraphResponseDto;
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
    public List<GraphResponseDto> graph(int year, User user){
        Long userId = user.getId();
        List<Object[]> objects = graphRepository.getGraph(year, userId);

        List<GraphResponseDto> graphResponseDtoList = new ArrayList<>();
        for (Object[] object : objects) {
            GraphResponseDto graphResponseDto = new GraphResponseDto(object);
            graphResponseDtoList.add(graphResponseDto);
        }
        return graphResponseDtoList;
    }
}
