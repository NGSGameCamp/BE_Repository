package com.imfine.ngs.game.tag;

import com.imfine.ngs.game.dto.mapper.GameMapper;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.dto.response.util.PagedSectionResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.service.MainPageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * 게임 태그 통합 테스트 클래스.
 * Service 레벨에서 태그 조회 및 변환 로직을 검증합니다.
 *
 * @author chan
 */
@ExtendWith(MockitoExtension.class)
class GameTagServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private MainPageService mainPageService;

    @Test
    @DisplayName("게임 목록 조회 시 태그 정보 포함 테스트")
    void getGamesWithTags() {
        // given
        Game game = createGameWithTags();
        GameSummaryResponse response = createGameSummaryResponse();
        Pageable pageable = PageRequest.of(0, 10);

        when(gameRepository.findByGameStatus(any(GameStatusType.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(game)));
        when(gameMapper.toSummaryResponseWithTags(game))
                .thenReturn(response);

        // when
        PagedSectionResponse result = mainPageService.getPopularGames(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGames()).hasSize(1);

        GameSummaryResponse gameResponse = result.getGames().get(0);
        assertThat(gameResponse.getTags()).hasSize(2);
        assertThat(gameResponse.getTags())
                .extracting(GameTagResponse::getTagType)
                .containsExactlyInAnyOrder(GameTagType.ACTION, GameTagType.RPG);
        assertThat(gameResponse.getTags())
                .extracting(GameTagResponse::getName)
                .containsExactlyInAnyOrder("액션", "롤플레잉");

        // verify
        verify(gameRepository, times(1)).findByGameStatus(GameStatusType.ACTIVE, pageable);
        verify(gameMapper, times(1)).toSummaryResponseWithTags(game);
    }

    @Test
    @DisplayName("여러 게임의 태그 정보 조회 테스트")
    void getMultipleGamesWithTags() {
        // given
        Game game1 = createGameWithTags();
        Game game2 = createGameWithSingleTag(GameTagType.STRATEGY);

        GameSummaryResponse response1 = createGameSummaryResponse();
        GameSummaryResponse response2 = createStrategyGameResponse();

        Pageable pageable = PageRequest.of(0, 10);

        when(gameRepository.findByGameStatus(any(GameStatusType.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(game1, game2)));
        when(gameMapper.toSummaryResponseWithTags(game1))
                .thenReturn(response1);
        when(gameMapper.toSummaryResponseWithTags(game2))
                .thenReturn(response2);

        // when
        PagedSectionResponse result = mainPageService.getPopularGames(pageable);

        // then
        assertThat(result.getGames()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // 첫 번째 게임
        assertThat(result.getGames().get(0).getTags()).hasSize(2);
        assertThat(result.getGames().get(0).getTagCount()).isEqualTo(2);

        // 두 번째 게임
        assertThat(result.getGames().get(1).getTags()).hasSize(1);
        assertThat(result.getGames().get(1).getTags().iterator().next().getTagType())
                .isEqualTo(GameTagType.STRATEGY);
    }

    @Test
    @DisplayName("태그가 없는 게임 조회 테스트")
    void getGameWithoutTags() {
        // given
        Game game = Game.builder()
                .id(1L)
                .name("태그 없는 게임")
                .price(10000L)
                .gameStatus(GameStatusType.ACTIVE)
                .build();

        GameSummaryResponse response = GameSummaryResponse.builder()
                .id(1L)
                .name("태그 없는 게임")
                .price(10000L)
                .gameStatus(GameStatusType.ACTIVE)
                .tags(new HashSet<>())
                .tagCount(0)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(gameRepository.findByGameStatus(any(GameStatusType.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(game)));
        when(gameMapper.toSummaryResponseWithTags(game))
                .thenReturn(response);

        // when
        PagedSectionResponse result = mainPageService.getPopularGames(pageable);

        // then
        assertThat(result.getGames()).hasSize(1);
        assertThat(result.getGames().get(0).getTags()).isEmpty();
        assertThat(result.getGames().get(0).getTagCount()).isEqualTo(0);
    }

    /**
     * 여러 태그를 가진 게임 생성 헬퍼 메서드
     */
    private Game createGameWithTags() {
        Game game = Game.builder()
                .id(1L)
                .name("테스트 게임")
                .price(10000L)
                .gameStatus(GameStatusType.ACTIVE)
                .build();

        Set<LinkedTag> tags = new HashSet<>();

        // ACTION 태그
        tags.add(createLinkedTag(GameTagType.ACTION));
        // RPG 태그
        tags.add(createLinkedTag(GameTagType.RPG));

        setField(game, "tags", tags);
        return game;
    }

    /**
     * 단일 태그를 가진 게임 생성 헬퍼 메서드
     */
    private Game createGameWithSingleTag(GameTagType tagType) {
        Game game = Game.builder()
                .id(2L)
                .name("전략 게임")
                .price(20000L)
                .gameStatus(GameStatusType.ACTIVE)
                .build();

        Set<LinkedTag> tags = new HashSet<>();
        tags.add(createLinkedTag(tagType));

        setField(game, "tags", tags);
        return game;
    }

    /**
     * LinkedTag 생성 헬퍼 메서드
     */
    private LinkedTag createLinkedTag(GameTagType tagType) {
        GameTag gameTag = new GameTag();
        setField(gameTag, "tagType", tagType);

        LinkedTag linkedTag = new LinkedTag();
        setField(linkedTag, "gameTag", gameTag);
        return linkedTag;
    }

    /**
     * GameSummaryResponse 생성 헬퍼 메서드 (ACTION, RPG 태그)
     */
    private GameSummaryResponse createGameSummaryResponse() {
        Set<GameTagResponse> tagResponses = Set.of(
                GameTagResponse.builder()
                        .tagType(GameTagType.ACTION)
                        .name("액션")
                        .description("빠른 반응과 실시간 전투가 특징인 게임")
                        .build(),
                GameTagResponse.builder()
                        .tagType(GameTagType.RPG)
                        .name("롤플레잉")
                        .description("캐릭터 성장과 스토리가 중심인 게임")
                        .build()
        );

        return GameSummaryResponse.builder()
                .id(1L)
                .name("테스트 게임")
                .price(10000L)
                .gameStatus(GameStatusType.ACTIVE)
                .tags(tagResponses)
                .tagCount(2)
                .environmentCount(0)
                .build();
    }

    /**
     * GameSummaryResponse 생성 헬퍼 메서드 (STRATEGY 태그)
     */
    private GameSummaryResponse createStrategyGameResponse() {
        Set<GameTagResponse> tagResponses = Set.of(
                GameTagResponse.builder()
                        .tagType(GameTagType.STRATEGY)
                        .name("전략")
                        .description("전술과 계획이 중요한 게임")
                        .build()
        );

        return GameSummaryResponse.builder()
                .id(2L)
                .name("전략 게임")
                .price(20000L)
                .gameStatus(GameStatusType.ACTIVE)
                .tags(tagResponses)
                .tagCount(1)
                .environmentCount(0)
                .build();
    }

    /**
     * Reflection을 사용한 필드 설정 헬퍼 메서드
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}