package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 배급사(publisher)가 게임({@link com.imfine.ngs.game.entity.Game} 관리 서비스 클래스.
 *
 */
@RequiredArgsConstructor
@Service
public class GameRegistrationService {

    // 게임 저장소
    private final GameRepository gameRepository;

    // 게임 등록
    public Game createGame(GameCreateRequest gameCreateRequest) {
        if(gameCreateRequest == null) {
            throw new IllegalArgumentException("Data is null");
        }
        return gameRepository.save( Game.builder()
                .name(gameCreateRequest.getName())
                .price(gameCreateRequest.getPrice())
                .tags(gameCreateRequest.getTags())
                .env(gameCreateRequest.getEnvironments())
                .gameStatus(gameCreateRequest.getGameStatus())
                .description(gameCreateRequest.getDescription())
                .thumbnailUrl(gameCreateRequest.getThumbnailUrl())
                .spec(gameCreateRequest.getSpec())
                .createdAt(LocalDateTime.now())
                .build());
    }
}
