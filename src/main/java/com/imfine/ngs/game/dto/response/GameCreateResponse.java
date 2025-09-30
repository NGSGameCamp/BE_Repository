package com.imfine.ngs.game.dto.response;

import com.imfine.ngs.game.enums.GameStatusType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class GameCreateResponse {
    private String name;
    private Long price;
    private GameStatusType gameStatus;
    private String description;
    private String thumbnailUrl;
    private String spec;
    private List<GameTagResponse> gameTagResponse;
    private List<EnvResponse> envResponse;
    private LocalDateTime createAt;
}