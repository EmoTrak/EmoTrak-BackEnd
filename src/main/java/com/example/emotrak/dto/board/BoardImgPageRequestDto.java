package com.example.emotrak.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardImgPageRequestDto {
    private boolean lastPage;
    private List<BoardImgRequestDto> contents;

    public BoardImgPageRequestDto(Page<BoardImgRequestDto> contents) {
        this.lastPage = !contents.hasNext();
        this.contents = contents.getContent();
    }
}
