package com.imfine.ngs.game.dto.response;

import lombok.*;

import java.util.List;

/**
 * 메인 페이지 게임 데이터 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@Builder
public class MainPageResponse {

    private PagedSectionResponse popularSection; // 인기 게임
    private PagedSectionResponse newSection; // 신규 게임
    private PagedSectionResponse recommendedSection; // 추천 게임
    private PagedSectionResponse discountSection; // 할인 게임

}