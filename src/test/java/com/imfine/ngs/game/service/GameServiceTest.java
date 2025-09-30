package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link GameService} 단위 테스트 클래스.
 * 게임 상세 조회 기능을 테스트합니다.
 *
 * @author chan
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameDetailMapper gameDetailMapper;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private GameDetailResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testGame = createTestGame();
        expectedResponse = createExpectedResponse();
    }

    @Test
    @DisplayName("게임 상세 조회 성공 - 정상 케이스")
    void getGameDetail_ExistingId_ReturnsGameDetailResponse() {
        // given
        Long gameId = 1L;
        when(gameRepository.findByIdWithDetails(gameId))
                .thenReturn(Optional.of(testGame));
        when(gameDetailMapper.toDetailResponse(testGame))
                .thenReturn(expectedResponse);

        // when
        GameDetailResponse result = gameService.getGameDetail(gameId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gameId);
        assertThat(result.getName()).isEqualTo("테스트 게임");
        assertThat(result.getPrice()).isEqualTo(30000L);
        assertThat(result.getTagNames()).containsExactlyInAnyOrder("액션", "RPG");
        assertThat(result.getDescription()).isEqualTo("테스트 설명");
        assertThat(result.getThumbnailUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(result.getSpec()).isEqualTo("최소 사양");
        assertThat(result.getPublisher()).isNull();
        assertThat(result.getReviewCount()).isEqualTo(5);
        assertThat(result.getAverageScore()).isEqualTo(3.0);
        assertThat(result.getMediaUrls()).isEmpty();

        // verify interactions
        verify(gameRepository, times(1)).findByIdWithDetails(gameId);
        verify(gameDetailMapper, times(1)).toDetailResponse(testGame);
    }

    @Test
    @DisplayName("존재하지 않는 게임 조회 시 EntityNotFoundException 발생")
    void getGameDetail_NonExistingId_ThrowsEntityNotFoundException() {
        // given
        Long nonExistingId = 999L;
        when(gameRepository.findByIdWithDetails(nonExistingId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gameService.getGameDetail(nonExistingId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Game not found")
                .hasMessageContaining(nonExistingId.toString());

        // verify repository was called but mapper was not
        verify(gameRepository, times(1)).findByIdWithDetails(nonExistingId);
        verify(gameDetailMapper, never()).toDetailResponse(any());
    }

    @Test
    @DisplayName("Repository와 Mapper 상호작용 검증")
    void getGameDetail_VerifyInteractions() {
        // given
        Long gameId = 1L;
        when(gameRepository.findByIdWithDetails(gameId))
                .thenReturn(Optional.of(testGame));
        when(gameDetailMapper.toDetailResponse(testGame))
                .thenReturn(expectedResponse);

        // when
        gameService.getGameDetail(gameId);

        // then - verify method calls with correct parameters
        verify(gameRepository).findByIdWithDetails(gameId);
        verify(gameDetailMapper).toDetailResponse(testGame);

        // verify no more interactions
        verifyNoMoreInteractions(gameRepository);
        verifyNoMoreInteractions(gameDetailMapper);
    }

    @Test
    @DisplayName("Mapper가 null을 반환하는 경우 처리")
    void getGameDetail_MapperReturnsNull_HandlesGracefully() {
        // given
        Long gameId = 1L;
        when(gameRepository.findByIdWithDetails(gameId))
                .thenReturn(Optional.of(testGame));
        when(gameDetailMapper.toDetailResponse(testGame))
                .thenReturn(null);

        // when
        GameDetailResponse result = gameService.getGameDetail(gameId);

        // then
        assertThat(result).isNull();

        verify(gameRepository, times(1)).findByIdWithDetails(gameId);
        verify(gameDetailMapper, times(1)).toDetailResponse(testGame);
    }

    /**
     * 테스트용 Game 엔티티 생성
     */
    private Game createTestGame() {
        Game game = new Game();
        setField(game, "id", 1L);
        setField(game, "name", "테스트 게임");
        setField(game, "price", 30000L);
        setField(game, "description", "테스트 설명");
        setField(game, "thumbnailUrl", "http://example.com/image.jpg");
        setField(game, "spec", "최소 사양");
        setField(game, "tags", createTestTags());
        setField(game, "reviews", createTestReviews());
        return game;
    }

    /**
     * 테스트용 LinkedTag Set 생성
     */
    private Set<LinkedTag> createTestTags() {
        Set<LinkedTag> tags = new HashSet<>();

        // ACTION 태그
        GameTag actionTag = new GameTag();
        setField(actionTag, "tagType", GameTagType.ACTION);
        LinkedTag actionLinkedTag = new LinkedTag();
        setField(actionLinkedTag, "gameTag", actionTag);
        tags.add(actionLinkedTag);

        // RPG 태그
        GameTag rpgTag = new GameTag();
        setField(rpgTag, "tagType", GameTagType.RPG);
        LinkedTag rpgLinkedTag = new LinkedTag();
        setField(rpgLinkedTag, "gameTag", rpgTag);
        tags.add(rpgLinkedTag);

        return tags;
    }

    /**
     * 테스트용 Review 리스트 생성
     */
    private List<Review> createTestReviews() {
        List<Review> reviews = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Review review = new Review();
            setField(review, "id", (long) i);
            setField(review, "score", i); // Integer 타입: 1, 2, 3, 4, 5
            setField(review, "content", "리뷰 " + i);
            reviews.add(review);
        }

        return reviews;
    }

    /**
     * 예상 응답 DTO 생성
     */
    private GameDetailResponse createExpectedResponse() {
        return GameDetailResponse.builder()
                .id(1L)
                .name("테스트 게임")
                .price(30000L)
                .tagNames(Set.of("액션", "RPG"))
                .description("테스트 설명")
                .thumbnailUrl("http://example.com/image.jpg")
                .spec("최소 사양")
                .publisher(null)
                .reviewCount(5)
                .averageScore(3.0)  // 평균: (1+2+3+4+5)/5 = 3.0
                .mediaUrls(new HashSet<>())
                .build();
    }

    /**
     * Reflection을 사용한 private 필드 설정 헬퍼 메서드
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}