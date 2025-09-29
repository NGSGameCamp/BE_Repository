package com.imfine.ngs.game.dto.response.util;

import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * {@link com.imfine.ngs.game.controller.NGSController} 메인 페이지에서 사용할 페이지네이션 정보를 포함한 {@link GameSummaryResponse} 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@Builder
public class PagedSectionResponse {
    private List<GameSummaryResponse> games;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
}
