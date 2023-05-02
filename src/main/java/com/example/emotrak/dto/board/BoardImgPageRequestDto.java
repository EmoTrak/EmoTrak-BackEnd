package com.example.emotrak.dto.board;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("contents")
    private List<BoardImgRequestDto> boardImgRequestDtoList;

    public BoardImgPageRequestDto(Page<BoardImgRequestDto> boardImgRequestDtoList) {
        this.lastPage = !boardImgRequestDtoList.hasNext();
        this.boardImgRequestDtoList = boardImgRequestDtoList.getContent();
    }
}
