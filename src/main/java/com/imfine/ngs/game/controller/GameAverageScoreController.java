package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameAverageScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * {@link com.imfine.ngs.game.entity.Game}의 평균평점으로 조회 응답 클래스.
 *
 * @author chan
 */
@Tag(name = "Game", description = "게임 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/games/averageScore")
@RestController
public class GameAverageScoreController {

    private final GameAverageScoreService averageScoreService;

    /**
     * 평균 평점 범위로 게임 조회
     *
     * @param minAverage 최소 평균 평점 (null 가능, 기본값 0.0)
     * @param maxAverage 최대 평균 평점 (null 가능, 기본값 5.0)
     * @param pageable   페이징 정보 (기본 5개)
     * @return 평균 평점 범위 내 게임 목록
     */
    @Operation(
            summary = "평균 평점으로 게임 조회",
            description = "지정한 평균 평점 범위 내의 게임을 조회합니다. minAverage와 maxAverage는 선택적으로 입력할 수 있습니다. 하나의 값만 입력하면 평균 이상의 값들을 조회합니다. (범위: 0.0 ~ 5.0)"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<GameCardResponse> getAverageScore(
            @Parameter(description = "최소 평균 평점", example = "3.5")
            @RequestParam(required = false) Double minAverage,
            @Parameter(description = "최대 평균 평점", example = "5.0")
            @RequestParam(required = false) Double maxAverage,
            @PageableDefault(size = 5) Pageable pageable) {

        return averageScoreService.findAverageScore(minAverage, maxAverage, pageable);
    }
}
