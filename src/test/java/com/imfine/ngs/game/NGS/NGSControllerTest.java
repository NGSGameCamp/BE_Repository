package com.imfine.ngs.game.NGS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imfine.ngs._global.config.security.jwt.JwtAuthenticationFilter;
import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.game.controller.NGSController;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.PagedSectionResponse;
import com.imfine.ngs.game.service.MainPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NGSController 테스트
 * 각 엔드포인트가 PagedSectionResponse를 반환하는 구조 테스트
 *
 * @author chan
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NGSController.class)
public class NGSControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MainPageService mainPageService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PagedSectionResponse mockPagedResponse;

    @BeforeEach
    void setUp() {
        // 테스트용 PagedSectionResponse 생성
        mockPagedResponse = createMockPagedResponse();
    }

    /**
     * Mock PagedSectionResponse 생성
     */
    private PagedSectionResponse createMockPagedResponse() {
        return createPagedResponse(5, 30);
    }

    /**
     * PagedSectionResponse 생성 헬퍼 메서드
     */
    private PagedSectionResponse createPagedResponse(int size, long totalElements) {
        List<GameSummaryResponse> games = createTestGames(size);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PagedSectionResponse.builder()
                .games(games)
                .currentPage(0)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasNext(totalPages > 1)
                .hasPrevious(false)
                .build();
    }

    /**
     * 테스트용 GameSummaryResponse 리스트 생성
     */
    private List<GameSummaryResponse> createTestGames(int count) {
        List<GameSummaryResponse> games = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            games.add(GameSummaryResponse.builder()
                    .id((long) i)
                    .name("테스트 게임 " + i)
                    .price(10000L * i)
                    .tag("RPG")
                    .thumbnailUrl("http://example.com/game" + i + ".jpg")
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .build());
        }

        return games;
    }

    @Test
    @DisplayName("메인 페이지 - 추천 게임 조회")
    void testNGSMainPage() throws Exception {
        // given
        when(mainPageService.getRecommendGames(any(Pageable.class))).thenReturn(mockPagedResponse);

        // when & then
        mockMvc.perform(get("/api/main"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                // PagedSectionResponse 구조 검증
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games.length()").value(5))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(6))
                .andExpect(jsonPath("$.totalElements").value(30))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));

        // 서비스 메서드 호출 확인
        verify(mainPageService, times(1)).getRecommendGames(any(Pageable.class));
    }

    @Test
    @DisplayName("인기 게임 페이지 조회")
    void testPopularGamesPage() throws Exception {
        // given
        PagedSectionResponse popularResponse = createPagedResponse(5, 100);
        when(mainPageService.getPopularGames(any(Pageable.class))).thenReturn(popularResponse);

        // when & then
        mockMvc.perform(get("/api/main/popular")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.totalPages").value(20));

        verify(mainPageService, times(1)).getPopularGames(any(Pageable.class));
    }

    @Test
    @DisplayName("신작 게임 페이지 조회")
    void testNewGamesPage() throws Exception {
        // given
        PagedSectionResponse newGamesResponse = createPagedResponse(5, 50);
        when(mainPageService.getNewGames(any(Pageable.class))).thenReturn(newGamesResponse);

        // when & then
        mockMvc.perform(get("/api/main/newGames")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(50))
                .andExpect(jsonPath("$.totalPages").value(10));

        verify(mainPageService, times(1)).getNewGames(any(Pageable.class));
    }

    @Test
    @DisplayName("추천 게임 페이지 조회")
    void testRecommendedGamesPage() throws Exception {
        // given
        PagedSectionResponse recommendedResponse = createPagedResponse(5, 30);
        when(mainPageService.getRecommendGames(any(Pageable.class))).thenReturn(recommendedResponse);

        // when & then
        mockMvc.perform(get("/api/main/recommended")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.totalElements").value(30));

        verify(mainPageService, times(1)).getRecommendGames(any(Pageable.class));
    }

    @Test
    @DisplayName("할인 게임 페이지 조회")
    void testDiscountGamesPage() throws Exception {
        // given
        PagedSectionResponse discountResponse = createPagedResponse(5, 20);
        when(mainPageService.getDiscountGames(any(Pageable.class))).thenReturn(discountResponse);

        // when & then
        mockMvc.perform(get("/api/main/discount")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.totalPages").value(4));

        verify(mainPageService, times(1)).getDiscountGames(any(Pageable.class));
    }

    @Test
    @DisplayName("빈 게임 목록 처리")
    void testEmptyGamesResponse() throws Exception {
        // given
        PagedSectionResponse emptyResponse = createEmptyPagedResponse();
        when(mainPageService.getRecommendGames(any(Pageable.class))).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/api/main"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    @DisplayName("API 경로 및 HTTP 메서드 검증")
    void testApiEndpoints() throws Exception {
        // given
        when(mainPageService.getRecommendGames(any(Pageable.class))).thenReturn(mockPagedResponse);

        // 잘못된 HTTP 메서드로 요청 - GlobalExceptionHandler가 500으로 변환
        mockMvc.perform(post("/api/main"))
                .andExpect(status().is5xxServerError());

        // 잘못된 경로로 요청 - GlobalExceptionHandler가 500으로 변환
        mockMvc.perform(get("/api/main/wrong"))
                .andExpect(status().is5xxServerError());

        // 올바른 경로와 메서드로 요청
        mockMvc.perform(get("/api/main"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("페이지 상세 정보 검증")
    void testPagedResponseDetails() throws Exception {
        // given
        when(mainPageService.getPopularGames(any(Pageable.class))).thenReturn(mockPagedResponse);

        // when & then
        MvcResult result = mockMvc.perform(get("/api/main/popular"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PagedSectionResponse response = objectMapper.readValue(content, PagedSectionResponse.class);

        // 상세 검증
        assertThat(response).isNotNull();
        assertThat(response.getGames()).hasSize(5);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getTotalPages()).isGreaterThan(0);
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.isHasPrevious()).isFalse();

        // 첫 번째 게임 상세 정보
        GameSummaryResponse firstGame = response.getGames().getFirst();
        assertThat(firstGame.getId()).isEqualTo(1L);
        assertThat(firstGame.getName()).isEqualTo("테스트 게임 1");
        assertThat(firstGame.getPrice()).isEqualTo(10000L);
    }

    /**
     * 빈 PagedSectionResponse 생성
     */
    private PagedSectionResponse createEmptyPagedResponse() {
        return PagedSectionResponse.builder()
                .games(new ArrayList<>())
                .currentPage(0)
                .totalPages(0)
                .totalElements(0L)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}