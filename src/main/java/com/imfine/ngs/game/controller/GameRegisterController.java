package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.response.GameCreateResponse;
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
    public GameCreateResponse createGame(@RequestBody GameCreateRequest gameCreateRequest) {
        return gameRegistrationService.createGame(gameCreateRequest);

    }
}
