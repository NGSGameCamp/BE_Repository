package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * GameService의 getPopular 메서드 테스트
 *
 * @author chan
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GameService - getPopular 메서드 테스트")
class GameServicePopularTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameCardMapper gameCardMapper;

    @InjectMocks
    private GameService gameService;

    private Game testGame1;
    private Game testGame2;
    private GameCardResponse cardResponse1;
    private GameCardResponse cardResponse2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // 페이징 설정
        pageable = PageRequest.of(0, 10);

        // 테스트 게임 생성
        testGame1 = createTestGame(1L, "게임1", 50000L);
        testGame2 = createTestGame(2L, "게임2", 30000L);

        // 테스트 응답 생성
        cardResponse1 = createTestCardResponse(1L, "게임1", 4.5);
        cardResponse2 = createTestCardResponse(2L, "게임2", 4.2);
    }

    @Test
    @DisplayName("파라미터가 null일 때 기본값이 적용되어야 한다")
    void getPopular_WithNullParameters_ShouldUseDefaultValues() {
        // given
        List<Game> games = List.of(testGame1, testGame2);
        Page<Game> gamePage = new PageImpl<>(games, pageable, games.size());

        given(gameRepository.findPopularGames(
                eq(GameStatusType.ACTIVE),
                eq(3L),    // 기본 리뷰 개수
                eq(3.5),   // 기본 평균 평점
                eq(pageable)
        )).willReturn(gamePage);

        given(gameCardMapper.toCardResponse(testGame1)).willReturn(cardResponse1);
        given(gameCardMapper.toCardResponse(testGame2)).willReturn(cardResponse2);

        // when
        Page<GameCardResponse> result = gameService.getPopular(pageable, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(cardResponse1, cardResponse2);

        // verify default values were used
        verify(gameRepository).findPopularGames(
                GameStatusType.ACTIVE,
                3L,    // 기본값 확인
                3.5,   // 기본값 확인
                pageable
        );
    }

    @Test
    @DisplayName("커스텀 파라미터가 제대로 전달되어야 한다")
    void getPopular_WithCustomParameters_ShouldUseProvidedValues() {
        // given
        Integer customMinReviews = 5;
        Double customMinScore = 4.0;

        List<Game> games = List.of(testGame1);
        Page<Game> gamePage = new PageImpl<>(games, pageable, 1);

        given(gameRepository.findPopularGames(
                eq(GameStatusType.ACTIVE),
                eq(5L),
                eq(4.0),
                eq(pageable)
        )).willReturn(gamePage);

        given(gameCardMapper.toCardResponse(testGame1)).willReturn(cardResponse1);

        // when
        Page<GameCardResponse> result = gameService.getPopular(
                pageable,
                customMinReviews,
                customMinScore
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(cardResponse1);

        // verify custom values were used
        verify(gameRepository).findPopularGames(
                GameStatusType.ACTIVE,
                5L,
                4.0,
                pageable
        );
    }

    @Test
    @DisplayName("빈 결과를 반환할 수 있어야 한다")
    void getPopular_WithNoMatchingGames_ShouldReturnEmptyPage() {
        // given
        Page<Game> emptyPage = Page.empty(pageable);

        given(gameRepository.findPopularGames(
                any(GameStatusType.class),
                anyLong(),
                anyDouble(),
                any(Pageable.class)
        )).willReturn(emptyPage);

        // when
        Page<GameCardResponse> result = gameService.getPopular(pageable, 10, 5.0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("페이징 정보가 올바르게 유지되어야 한다")
    void getPopular_ShouldMaintainPagingInformation() {
        // given
        Pageable customPageable = PageRequest.of(2, 5); // 3번째 페이지, 5개씩
        List<Game> games = List.of(testGame1);
        Page<Game> gamePage = new PageImpl<>(games, customPageable, 50); // 총 50개 중 1개

        given(gameRepository.findPopularGames(
                any(GameStatusType.class),
                anyLong(),
                anyDouble(),
                eq(customPageable)
        )).willReturn(gamePage);

        given(gameCardMapper.toCardResponse(any(Game.class))).willReturn(cardResponse1);

        // when
        Page<GameCardResponse> result = gameService.getPopular(customPageable, null, null);

        // then
        assertThat(result.getNumber()).isEqualTo(2); // 페이지 번호
        assertThat(result.getSize()).isEqualTo(5); // 페이지 크기
        assertThat(result.getTotalElements()).isEqualTo(50); // 총 요소 수
        assertThat(result.getTotalPages()).isEqualTo(10); // 총 페이지 수 (50/5)
    }

    @Test
    @DisplayName("부분적인 파라미터 설정도 처리할 수 있어야 한다")
    void getPopular_WithPartialParameters_ShouldHandleCorrectly() {
        // given - minReviews만 설정, minScore는 null
        Integer customMinReviews = 7;

        List<Game> games = List.of(testGame1);
        Page<Game> gamePage = new PageImpl<>(games, pageable, 1);

        given(gameRepository.findPopularGames(
                eq(GameStatusType.ACTIVE),
                eq(7L),
                eq(3.5),  // 기본값
                eq(pageable)
        )).willReturn(gamePage);

        given(gameCardMapper.toCardResponse(testGame1)).willReturn(cardResponse1);

        // when
        Page<GameCardResponse> result = gameService.getPopular(
                pageable,
                customMinReviews,
                null  // minScore는 null
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        // verify
        verify(gameRepository).findPopularGames(
                GameStatusType.ACTIVE,
                7L,    // 커스텀 값
                3.5,   // 기본값
                pageable
        );
    }

    // 헬퍼 메서드들
    private Game createTestGame(Long id, String name, Long price) {
        return Game.builder()
                .id(id)
                .name(name)
                .price(price)
                .gameStatus(GameStatusType.ACTIVE)
                .tags(new HashSet<>())
                .env(new HashSet<>())
                .reviews(new ArrayList<>())
                .discounts(new ArrayList<>())
                .build();
    }

    private GameCardResponse createTestCardResponse(Long id, String name, Double avgScore) {
        return GameCardResponse.builder()
                .id(id)
                .name(name)
                .price(50000L)
                .discountRate(0)
                .tags(List.of("액션", "RPG"))
                .publisherId(1L)
                .publisherName("테스트 퍼블리셔")
                .reviewCount(5)
                .averageScore(avgScore)
                .releaseDate(LocalDateTime.now().toLocalDate())
                .thumbnailUrl("test-thumbnail.jpg")
                .build();
    }
}