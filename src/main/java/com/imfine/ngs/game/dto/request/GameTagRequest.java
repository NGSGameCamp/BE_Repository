package com.imfine.ngs.game.dto.request;


import com.imfine.ngs.game.enums.GameTagType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameTagRequest {
    private GameTagType gameTagType;
}
