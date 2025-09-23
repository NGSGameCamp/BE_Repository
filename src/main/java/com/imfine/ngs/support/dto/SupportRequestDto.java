package com.imfine.ngs.support.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SupportRequestDto {
    private long gameId;
    private long orderId;
    private String content;

    @Builder
    public SupportRequestDto(long gameId, long orderId, String content) {
        this.gameId = gameId;
        this.orderId = orderId;
        this.content = content;
    }
}
