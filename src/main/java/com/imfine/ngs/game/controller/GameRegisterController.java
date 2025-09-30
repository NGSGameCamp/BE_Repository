package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.request.EnvRequest;
import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.request.GameTagRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.service.GameRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publisher")
public class GameRegisterController {
    private final GameRegistrationService gameRegistrationService;

    @PostMapping("/game")
    public Game createGame(@RequestBody GameCreateRequest gameCreateRequest) {
        return gameRegistrationService.createGame(gameCreateRequest);

    }
}
