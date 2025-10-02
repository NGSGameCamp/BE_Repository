package com.imfine.ngs.game.dto.response.page;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 페이징 응답 dto 크랠스.
 *
 * @param <GameCardResponse>
 * @author chan
 */
@Getter
public class GamePageResponse<GameCardResponse> {

    private List<GameCardResponse> content;        // 실제 데이터
    private int pageNumber;         // 현재 페이지 번호
    private int pageSize;           // 페이지 크기
    private long totalElements;     // 전체 데이터 수
    private int totalPages;         // 전체 페이지 수
    private boolean last;           // 마지막 페이지 여부

    @Builder
    public GamePageResponse(List<GameCardResponse> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
