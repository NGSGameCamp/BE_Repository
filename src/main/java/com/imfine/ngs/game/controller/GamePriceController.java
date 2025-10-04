package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GamePriceService;
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
 * {@link com.imfine.ngs.game.entity.Game} 게임 가격별 조회.
 *
 * @author chan
 */
@Tag(name = "Game Price", description = "게임 가격 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/games/price")
@RestController
public class GamePriceController {

    private final GamePriceService gamePriceService;

    /**
     * 모든 게임을 가격 오름차순으로 조회
     *
     * @param pageable 페이징 정보 (기본 5개)
     * @return 가격순 게임 목록
     */
    @Operation(
            summary = "모든 게임 가격순 조회",
            description = "모든 활성 게임을 가격 오름차순으로 조회합니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public Page<GameCardResponse> getAllPriceGame(@PageableDefault(size = 5) Pageable pageable) {
        return gamePriceService.findAllPriceGame(pageable);
    }

    /**
     * 가격 범위 내 게임 조회
     *
     * @param minPrice 최소 가격 (null 가능, 기본값 0)
     * @param maxPrice 최대 가격 (null 가능, 기본값 무제한)
     * @param pageable 페이징 정보 (기본 5개)
     * @return 가격 범위 내 게임 목록
     */
    @Operation(
            summary = "가격 범위로 게임 조회",
            description = "지정한 가격 범위 내의 게임을 조회합니다. minPrice와 maxPrice는 선택적으로 입력할 수 있습니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/range")
    public Page<GameCardResponse> getPriceRangeGames(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(size = 5) Pageable pageable) {

        // 예외처리: minPrice가 maxPrice보다 큰 경우
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("최소 가격은 최대 가격보다 클 수 없습니다.");
        }

        return gamePriceService.findPriceRangeGames(minPrice, maxPrice, pageable);
    }
}
