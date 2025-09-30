package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.request.EnvRequest;
import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.request.GameTagRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.repository.tag.GameTagRepository;
import com.imfine.ngs.game.repository.tag.LinkedTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 배급사(publisher)가 게임({@link com.imfine.ngs.game.entity.Game} 관리 서비스 클래스.
 *
 */
@RequiredArgsConstructor
@Service
public class GameRegistrationService {

    // 게임 저장소
    private final GameRepository gameRepository;
    private final GameTagService gameTagService;
    private final LinkedTagService linkedTagService;
    private final EnvService envService;
    private final LinkedEnvService linkedEnvService;

    // 게임 등록
    public Game createGame(GameCreateRequest gameCreateRequest) {
        if(gameCreateRequest == null) {
            throw new IllegalArgumentException("Data is null");
        }

        Game saveGame = gameRepository.save( Game.builder()
                .name(gameCreateRequest.getName())
                .price(gameCreateRequest.getPrice())
                .gameStatus(gameCreateRequest.getGameStatus())
                .description(gameCreateRequest.getDescription())
                .thumbnailUrl(gameCreateRequest.getThumbnailUrl())
                .spec(gameCreateRequest.getSpec())
                .createdAt(LocalDateTime.now())
                .build());


        List<GameTag> gameTags = gameTagService.findByGameTagTypes(gameCreateRequest.getGameTagRequest());
        linkedTagService.createLinkedTags(gameTags, saveGame);

        List<Env> envs = envService.findByEnvTypes(gameCreateRequest.getEnvRequest());
        linkedEnvService.createLinkedEnvs(envs, saveGame);

        return saveGame;
    }
}
