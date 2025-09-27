//package com.imfine.ngs.game.NGS;
//
//import com.imfine.ngs.game.dto.mapper.GameMapper;
//import com.imfine.ngs.game.dto.response.GameSummaryResponse;
//import com.imfine.ngs.game.dto.response.MainPageResponse;
//import com.imfine.ngs.game.dto.response.PagedSectionResponse;
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.repository.GameRepository;
//import com.imfine.ngs.game.service.MainPageService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
///**
// * 메인 페이지 서비스 테스트
// * {@link MainPageService}의 게임 조회 로직을 검증합니다.
// *
// * @author chan
// */
//@ExtendWith(MockitoExtension.class)
//class MainPageServiceTest {
//
//    @InjectMocks
//    private MainPageService mainPageService;
//
//    @Mock
//    private GameRepository gameRepository;
//
//    @Mock
//    private GameMapper gameMapper;
//
//    private List<Game> testGames;
//    private List<GameSummaryResponse> testGameSummaries;
//
//    @BeforeEach
//    void setUp() {
//        testGames = createTestGames();
//        testGameSummaries = createTestGameSummaries();
//    }
//
//    /**
//     * 테스트용 Game 엔티티 생성
//     */
//    private List<Game> createTestGames() {
//        List<Game> games = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            Game game = Game.builder()
//                    .id((long) i)
//                    .name("테스트게임" + i)
//                    .price(10000L * i)
//                    .tag("ACTION")
//                    .thumbnailUrl("http://example.com/game" + i + ".jpg")
//                    .description("테스트 게임 설명 " + i)
//                    .createdAt(LocalDateTime.now().minusDays(i))
//                    .isActive(true)
//                    .build();
//            games.add(game);
//        }
//        return games;
//    }
//
//    /**
//     * 테스트용 GameSummaryResponse DTO 생성
//     */
//    private List<GameSummaryResponse> createTestGameSummaries() {
//        return testGames.stream()
//                .map(game -> GameSummaryResponse.builder()
//                        .id(game.getId())
//                        .name(game.getName())
//                        .price(game.getPrice())
//                        .tag(game.getTag())
//                        .thumbnailUrl(game.getThumbnailUrl())
//                        .description(game.getDescription())
//                        .isActive(game.isActive())
//                        .createdAt(game.getCreatedAt())
//                        .environmentCount(3)
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * PagedSectionResponse 생성 헬퍼
//     */
//    private PagedSectionResponse createPagedSectionResponse(
//            List<GameSummaryResponse> games,
//            int page,
//            int size,
//            long total,
//            SectionType sectionType) {
//
//        return PagedSectionResponse.builder()
//                .games(games)
//                .currentPage(page)
//                .totalPages((int) Math.ceil((double) total / size))
//                .totalElements(total)
//                .hasNext(page < (int) Math.ceil((double) total / size) - 1)
//                .hasPrevious(page > 0)
//                .sectionType(sectionType)
//                .build();
//    }
//
//    @Test
//    @DisplayName("메인 페이지 초기 데이터 조회 - 성공")
//    void getInitialPageData_Success() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(0, 5),
//            pageable,
//            testGames.size()
//        );
//
//        when(gameRepository.findByIsActiveTrue(any(Pageable.class)))
//                .thenReturn(gamePage);
//        when(gameRepository.findByIsActiveTrueAndCreatedAtAfter(any(LocalDateTime.class), any(Pageable.class)))
//                .thenReturn(gamePage);
//
//        PagedSectionResponse mockResponse = createPagedSectionResponse(
//            testGameSummaries.subList(0, 5),
//            0, 5, 10, SectionType.POPULAR
//        );
//
//        when(gameMapper.convertToPagedResponse(any(Page.class), any(SectionType.class)))
//                .thenReturn(mockResponse);
//
//        // when
//        MainPageResponse response = mainPageService.getInitialPageData();
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getPopularSection()).isNotNull();
//        assertThat(response.getNewSection()).isNotNull();
//        assertThat(response.getRecommendedSection()).isNotNull();
//        assertThat(response.getDiscountSection()).isNotNull();
//
//        verify(gameRepository, times(3)).findByIsActiveTrue(any(Pageable.class));
//        verify(gameRepository, times(1)).findByIsActiveTrueAndCreatedAtAfter(any(LocalDateTime.class), any(Pageable.class));
//        verify(gameMapper, times(4)).convertToPagedResponse(any(Page.class), any(SectionType.class));
//    }
//
//    @Test
//    @DisplayName("인기 게임 페이징 조회")
//    void getPopularGames_WithPaging() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(0, 5),
//            pageable,
//            testGames.size()
//        );
//
//        when(gameRepository.findByIsActiveTrue(pageable)).thenReturn(gamePage);
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            testGameSummaries.subList(0, 5),
//            0, 5, 10, SectionType.POPULAR
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.POPULAR))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getPopularGames(pageable);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getSectionType()).isEqualTo(SectionType.POPULAR);
//        assertThat(response.getGames()).hasSize(5);
//        assertThat(response.getCurrentPage()).isEqualTo(0);
//        assertThat(response.getTotalElements()).isEqualTo(10);
//        assertThat(response.isHasNext()).isTrue();
//        assertThat(response.isHasPrevious()).isFalse();
//
//        verify(gameRepository).findByIsActiveTrue(pageable);
//        verify(gameMapper).convertToPagedResponse(gamePage, SectionType.POPULAR);
//    }
//
//    @Test
//    @DisplayName("신작 게임 조회 - 최근 90일")
//    void getNewGames_Recent90Days() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5);
//        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusDays(90);
//
//        // 최근 게임만 생성
//        List<Game> recentGames = testGames.stream()
//                .filter(g -> g.getCreatedAt().isAfter(threeMonthsAgo))
//                .collect(Collectors.toList());
//
//        Page<Game> gamePage = new PageImpl<>(recentGames, pageable, recentGames.size());
//
//        when(gameRepository.findByIsActiveTrueAndCreatedAtAfter(any(LocalDateTime.class), eq(pageable)))
//                .thenReturn(gamePage);
//
//        List<GameSummaryResponse> recentSummaries = testGameSummaries.stream()
//                .filter(g -> g.getCreatedAt().isAfter(threeMonthsAgo))
//                .collect(Collectors.toList());
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            recentSummaries,
//            0, 5, recentSummaries.size(), SectionType.NEW
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.NEW))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getNewGames(pageable);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getSectionType()).isEqualTo(SectionType.NEW);
//        assertThat(response.getGames()).allSatisfy(game ->
//            assertThat(game.getCreatedAt()).isAfter(threeMonthsAgo)
//        );
//
//        verify(gameRepository).findByIsActiveTrueAndCreatedAtAfter(any(LocalDateTime.class), eq(pageable));
//        verify(gameMapper).convertToPagedResponse(gamePage, SectionType.NEW);
//    }
//
//    @Test
//    @DisplayName("추천 게임 페이징 조회")
//    void getRecommendedGames_WithPaging() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(0, 5),
//            pageable,
//            testGames.size()
//        );
//
//        when(gameRepository.findByIsActiveTrue(pageable)).thenReturn(gamePage);
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            testGameSummaries.subList(0, 5),
//            0, 5, 10, SectionType.RECOMMENDED
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.RECOMMENDED))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getRecommendGames(pageable);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getSectionType()).isEqualTo(SectionType.RECOMMENDED);
//        assertThat(response.getGames()).hasSize(5);
//        assertThat(response.getTotalPages()).isEqualTo(2);
//
//        verify(gameRepository).findByIsActiveTrue(pageable);
//        verify(gameMapper).convertToPagedResponse(gamePage, SectionType.RECOMMENDED);
//    }
//
//    @Test
//    @DisplayName("할인 게임 페이징 조회")
//    void getDiscountGames_WithPaging() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(0, 5),
//            pageable,
//            testGames.size()
//        );
//
//        when(gameRepository.findByIsActiveTrue(pageable)).thenReturn(gamePage);
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            testGameSummaries.subList(0, 5),
//            0, 5, 10, SectionType.DISCOUNT
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.DISCOUNT))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getDiscountGames(pageable);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getSectionType()).isEqualTo(SectionType.DISCOUNT);
//        assertThat(response.getGames()).hasSize(5);
//
//        verify(gameRepository).findByIsActiveTrue(pageable);
//        verify(gameMapper).convertToPagedResponse(gamePage, SectionType.DISCOUNT);
//    }
//
//    @Test
//    @DisplayName("페이지네이션 - 다음 페이지 존재 여부 확인")
//    void pagination_HasNextPage() {
//        // given
//        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지, 5개씩
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(0, 5),
//            pageable,
//            10 // 전체 10개
//        );
//
//        when(gameRepository.findByIsActiveTrue(pageable)).thenReturn(gamePage);
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            testGameSummaries.subList(0, 5),
//            0, 5, 10, SectionType.POPULAR
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.POPULAR))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getPopularGames(pageable);
//
//        // then
//        assertThat(response.isHasNext()).isTrue(); // 다음 페이지 존재
//        assertThat(response.isHasPrevious()).isFalse(); // 이전 페이지 없음
//        assertThat(response.getTotalPages()).isEqualTo(2); // 총 2페이지
//    }
//
//    @Test
//    @DisplayName("페이지네이션 - 마지막 페이지")
//    void pagination_LastPage() {
//        // given
//        Pageable pageable = PageRequest.of(1, 5); // 두번째 페이지
//        Page<Game> gamePage = new PageImpl<>(
//            testGames.subList(5, 10),
//            pageable,
//            10
//        );
//
//        when(gameRepository.findByIsActiveTrue(pageable)).thenReturn(gamePage);
//
//        PagedSectionResponse expectedResponse = createPagedSectionResponse(
//            testGameSummaries.subList(5, 10),
//            1, 5, 10, SectionType.POPULAR
//        );
//
//        when(gameMapper.convertToPagedResponse(gamePage, SectionType.POPULAR))
//                .thenReturn(expectedResponse);
//
//        // when
//        PagedSectionResponse response = mainPageService.getPopularGames(pageable);
//
//        // then
//        assertThat(response.isHasNext()).isFalse(); // 다음 페이지 없음
//        assertThat(response.isHasPrevious()).isTrue(); // 이전 페이지 존재
//        assertThat(response.getCurrentPage()).isEqualTo(1);
//    }
//}