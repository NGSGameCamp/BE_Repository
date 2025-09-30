package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.response.EnvResponse;
import com.imfine.ngs.game.dto.response.GameCreateResponse;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final GameTagService gameTagService;
    private final LinkedTagService linkedTagService;
    private final EnvService envService;
    private final LinkedEnvService linkedEnvService;

    // 게임 등록
    public GameCreateResponse createGame(GameCreateRequest gameCreateRequest, long userId) {
        if(gameCreateRequest == null) {
            throw new IllegalArgumentException("Data is null");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 userId: " + userId));

        Game saveGame = gameRepository.save( Game.builder()
                .name(gameCreateRequest.getName())
                .price(gameCreateRequest.getPrice())
                .gameStatus(gameCreateRequest.getGameStatus())
                .description(gameCreateRequest.getDescription())
                .thumbnailUrl(gameCreateRequest.getThumbnailUrl())
                .spec(gameCreateRequest.getSpec())
                .introduction(gameCreateRequest.getIntroduction())
                .publisher(user)
                .createdAt(LocalDateTime.now())
                .build());


        List<GameTag> gameTags = gameTagService.findByGameTagTypes(gameCreateRequest.getGameTagRequest());
        linkedTagService.createLinkedTags(gameTags, saveGame);

        List<Env> envs = envService.findByEnvTypes(gameCreateRequest.getEnvRequest());
        linkedEnvService.createLinkedEnvs(envs, saveGame);

        return createResponse(saveGame, gameTags, envs);
    }

    public GameCreateResponse createResponse(Game saveGame, List<GameTag> tags, List<Env> envs) {
        return GameCreateResponse.builder()
                .name(saveGame.getName())
                .price(saveGame.getPrice())
                .gameStatus(saveGame.getGameStatus())
                .description(saveGame.getDescription())
                .thumbnailUrl(saveGame.getThumbnailUrl())
                .spec(saveGame.getSpec())
                .gameTagResponse(tags.stream()
                        .map(tag -> GameTagResponse.builder().gameTagType(tag.getTagType().name()).build())
                        .toList())
                .envResponse(envs.stream()
                        .map(env -> EnvResponse.builder().envType(env.getEnvType().name()).build())
                        .toList())
                .createAt(saveGame.getCreatedAt())
                .publisherId(saveGame.getPublisher().getId())
                .introduction(saveGame.getIntroduction())
                .build();
    }
}
