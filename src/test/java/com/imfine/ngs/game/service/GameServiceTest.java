package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.env.util.LinkedEnvId;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        assertThat(result.getTags()).containsExactlyInAnyOrder("액션", "RPG");
        assertThat(result.getDescription()).isEqualTo("테스트 설명");
        assertThat(result.getIntroduction()).isEqualTo("테스트 게임 소개");
        assertThat(result.getThumbnailUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(result.getSpec()).isEqualTo("최소 사양");
        assertThat(result.getReviewCount()).isEqualTo(5);
        assertThat(result.getAverageScore()).isEqualTo(3.0);
        assertThat(result.getMediaUrls()).containsExactly("image1.jpg", "image2.jpg");
        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.getDiscountRate()).isEqualTo(30);
        assertThat(result.getPublisherId()).isEqualTo(100L);
        assertThat(result.getPublisherName()).isEqualTo("테스트 배급사");
        assertThat(result.getEnv()).containsExactlyInAnyOrder("Windows", "Mac", "Linux");

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
        setField(game, "introduction", "테스트 게임 소개");
        setField(game, "thumbnailUrl", "http://example.com/image.jpg");
        setField(game, "spec", "최소 사양");
        setField(game, "tags", createTestTags());
        setField(game, "env", createTestEnvs());
        setField(game, "reviews", createTestReviews());
        setField(game, "publisher", createTestPublisher());
        setField(game, "discounts", createTestDiscounts());
        setField(game, "mediaUrls", Arrays.asList("image1.jpg", "image2.jpg"));
        setField(game, "createdAt", LocalDateTime.of(2024, 1, 1, 0, 0));
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
     * 테스트용 LinkedEnv Set 생성
     */
    private Set<LinkedEnv> createTestEnvs() {
        Set<LinkedEnv> envs = new HashSet<>();

        // Windows 환경
        Env windowsEnv = new Env();
        setField(windowsEnv, "id", 1L);
        setField(windowsEnv, "envType", EnvType.WINDOWS);
        LinkedEnv windowsLinkedEnv = new LinkedEnv();
        setField(windowsLinkedEnv, "env", windowsEnv);
        envs.add(windowsLinkedEnv);

        // Mac 환경
        Env macEnv = new Env();
        setField(macEnv, "id", 2L);
        setField(macEnv, "envType", EnvType.MAC);
        LinkedEnv macLinkedEnv = new LinkedEnv();
        setField(macLinkedEnv, "env", macEnv);
        envs.add(macLinkedEnv);

        // Linux 환경
        Env linuxEnv = new Env();
        setField(linuxEnv, "id", 3L);
        setField(linuxEnv, "envType", EnvType.LINUX);
        LinkedEnv linuxLinkedEnv = new LinkedEnv();
        setField(linuxLinkedEnv, "env", linuxEnv);
        envs.add(linuxLinkedEnv);

        return envs;
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
     * 테스트용 Publisher (User) 생성
     */
    private User createTestPublisher() {
        User publisher = new User();
        setField(publisher, "id", 100L);
        setField(publisher, "name", "테스트 배급사");
        setField(publisher, "email", "publisher@test.com");
        return publisher;
    }

    /**
     * 테스트용 할인 리스트 생성
     */
    private List<SingleGameDiscount> createTestDiscounts() {
        List<SingleGameDiscount> discounts = new ArrayList<>();

        // 현재 유효한 할인
        SingleGameDiscount activeDiscount = new SingleGameDiscount();
        setField(activeDiscount, "id", 1L);
        setField(activeDiscount, "discountRate", new BigDecimal("30"));
        setField(activeDiscount, "createdAt", LocalDateTime.now().minusDays(1));
        setField(activeDiscount, "expiresAt", LocalDateTime.now().plusDays(7));
        discounts.add(activeDiscount);

        // 만료된 할인
        SingleGameDiscount expiredDiscount = new SingleGameDiscount();
        setField(expiredDiscount, "id", 2L);
        setField(expiredDiscount, "discountRate", new BigDecimal("50"));
        setField(expiredDiscount, "createdAt", LocalDateTime.now().minusDays(10));
        setField(expiredDiscount, "expiresAt", LocalDateTime.now().minusDays(1));
        discounts.add(expiredDiscount);

        return discounts;
    }

    /**
     * 예상 응답 DTO 생성
     */
    private GameDetailResponse createExpectedResponse() {
        return GameDetailResponse.builder()
                .id(1L)
                .name("테스트 게임")
                .price(30000L)
                .tags(Arrays.asList("액션", "RPG"))
                .description("테스트 설명")
                .introduction("테스트 게임 소개")
                .thumbnailUrl("http://example.com/image.jpg")
                .spec("최소 사양")
                .reviewCount(5)
                .averageScore(3.0)  // 평균: (1+2+3+4+5)/5 = 3.0
                .mediaUrls(Arrays.asList("image1.jpg", "image2.jpg"))
                .releaseDate(LocalDate.of(2024, 1, 1))
                .discountRate(30)  // 현재 유효한 할인율
                .publisherId(100L)  // 배급사 ID
                .publisherName("테스트 배급사")  // 배급사 이름
                .env(Arrays.asList("Windows", "Mac", "Linux"))  // 게임 환경
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