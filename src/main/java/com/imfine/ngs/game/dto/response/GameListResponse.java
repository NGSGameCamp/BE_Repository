package com.imfine.ngs.game.dto.response;

import lombok.*;
import java.util.List;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 목록 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameListResponse {

    private List<GameResponse> games;
    private PageInfo pageInfo;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
