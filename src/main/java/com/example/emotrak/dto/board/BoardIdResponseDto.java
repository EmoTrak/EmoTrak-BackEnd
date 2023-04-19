package com.example.emotrak.dto.board;

import com.example.emotrak.entity.Daily;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardIdResponseDto {
    private Long id;

    public BoardIdResponseDto(Daily daily) {
        this.id = daily.getId();
    }
}
