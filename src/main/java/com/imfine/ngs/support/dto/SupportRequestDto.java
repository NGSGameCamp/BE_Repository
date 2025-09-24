package com.imfine.ngs.support.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SupportRequestDto {
    private long gameId;
    private long orderId;
    private String title;
    private String content;

    @Builder
    public SupportRequestDto(long gameId, long orderId, String title,String content) {
        this.gameId = gameId;
        this.orderId = orderId;
        this.title = title;
        this.content = content;
    }
}
