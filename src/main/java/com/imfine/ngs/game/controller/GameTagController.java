package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * {@link com.imfine.ngs.game.entity.Game} 게임 태그별 조회.
 *
 * @author chan
 */
@Tag(name = "Game", description = "게임 관련 API")
@RequiredArgsConstructor
@RequestMapping(("/api/games/tags"))
@RestController
public class GameTagController {

    private final GameService gameService;

    /**
     * 게임 태그로 필터링 조회
     *
     * @param tagCodes 태그 코드 리스트 (모든 태그를 포함하는 게임만 조회)
     * @param pageable 페이징 정보 (기본 5개)
     * @return 태그 필터링된 게임 목록
     */
    @Operation(
            summary = "태그로 게임 조회",
            description = "지정한 모든 태그를 포함하는 게임을 조회합니다. (AND 조건)"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<GameCardResponse> getGameTags(
            @Parameter(description = "태그 코드 리스트", example = "action,rpg")
            @RequestParam List<String> tagCodes,
            @PageableDefault(size = 5) Pageable pageable) {

        return gameService.findByGameTags(tagCodes, pageable);
    }

}
