package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameReleasedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * {@link com.imfine.ngs.game.entity.Game} 게임을 createAt 날짜 요청/응답 클래스.
 *
 * @author chan
 */
@Tag(name = "Game", description = "게임 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/games/release")
@RestController
public class GameReleaseController {

    // gameReleasedService
    private final GameReleasedService releasedService;

    // 모든 기간 조회
    @Operation(
            summary = "등록된 게임 목록 조회",
            description = "모든 기간의 게임을 조회합니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public Page<GameCardResponse> getAllReleasedGames(@PageableDefault(size = 5) Pageable pageable) {

        // 모든 게임 조회 (기본: 최신 게임 순으로 정렬)
        return releasedService.findAllReleased(pageable);
    }

    // 최근 N개월 이내 게임 조회 (1개월, 3개월, 1년)
    @Operation(
            summary = "기간별 게임 목록 조회",
            description = "최근 N개월 이내에 출시된 게임을 조회합니다. (1=1개월, 3=3개월, 12=1년)"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{months}")
    public Page<GameCardResponse> getMonthlyGames(
            @PathVariable int months,
            @PageableDefault(size = 5) Pageable pageable) {

        // 조건에 맞는 게임 조회
        return releasedService.findByMonth(months, pageable);
    }
}
