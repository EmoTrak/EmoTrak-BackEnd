package com.example.emotrak.dto.like;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeResponseDto {
    private boolean hasLike;
    private int likesCount;

    public LikeResponseDto(boolean hasLike, int likesCount) {
        this.hasLike = hasLike;
        this.likesCount = likesCount;
    }
}
