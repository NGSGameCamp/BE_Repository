package com.imfine.ngs.game.dto.response;

import lombok.*;
import java.util.List;

/**
 * 메인 페이지 게임 데이터 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainPageResponse {

    private List<GameResponse> popularGames;
    private List<GameResponse> newGames;
    private List<GameResponse> recommendedGames;
    private List<GameResponse> discountedGames;

    private int popularCount;
    private int newCount;
    private int recommendedCount;
    private int discountedCount;
}