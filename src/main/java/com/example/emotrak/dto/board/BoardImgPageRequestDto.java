package com.example.emotrak.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardImgPageRequestDto {
    private boolean lastPage;
    private List<BoardImgRequestDto> contents;

    public BoardImgPageRequestDto(boolean lastPage, List<BoardImgRequestDto> contents) {
        this.lastPage = lastPage;
        this.contents = contents;
    }
}
