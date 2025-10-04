package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(("/api/games/tags"))
@RestController
public class GameTagController {

    private final GameService gameService;

    // 게임 태그 여러개 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<GameCardResponse> getGameTags(@RequestParam List<String> tagCodes, Pageable pageable) {

        // 서비스 호출
        return gameService.getGameTags(tagCodes, pageable);
    }

}
