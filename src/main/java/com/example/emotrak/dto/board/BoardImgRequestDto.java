package com.example.emotrak.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardImgRequestDto {
    private Long Id;
    private String imgUrl;

    public BoardImgRequestDto(Object[] object) {
        this.Id = ((BigInteger) object[0]).longValue();
        this.imgUrl = (String) object[1];
    }
}
