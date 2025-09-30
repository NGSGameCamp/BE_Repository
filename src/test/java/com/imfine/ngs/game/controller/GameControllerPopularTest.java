package com.imfine.ngs.game.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameService;
import com.imfine.ngs.user.oauth.CookieAuthorizationRequestRepository;
import com.imfine.ngs.user.oauth.OAuth2AuthenticationFailureHandler;
import com.imfine.ngs.user.oauth.OAuth2AuthenticationSuccessHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

/**
 * GameController의 /popular 엔드포인트 테스트
 *
 * @author chan
 */
@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화
@DisplayName("GameController - /popular 엔드포인트 테스트")
class GameControllerPopularTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockBean
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @MockBean
    private CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private GameCardResponse testCardResponse1;
    private GameCardResponse testCardResponse2;
    private Page<GameCardResponse> testPage;

    @BeforeEach
    void setUp() {
        // 테스트 응답 데이터 생성
        testCardResponse1 = GameCardResponse.builder()
                .id(1L)
                .name("인기 게임 1")
                .price(50000L)
                .discountRate(10)
                .tags(List.of("액션", "RPG"))
                .publisherId(1L)
                .publisherName("퍼블리셔 A")
                .reviewCount(10)
                .averageScore(4.5)
                .releaseDate(LocalDate.now().minusDays(30))
                .thumbnailUrl("http://example.com/game1.jpg")
                .build();

        testCardResponse2 = GameCardResponse.builder()
                .id(2L)
                .name("인기 게임 2")
                .price(30000L)
                .discountRate(20)
                .tags(List.of("전략", "시뮬레이션"))
                .publisherId(2L)
                .publisherName("퍼블리셔 B")
                .reviewCount(8)
                .averageScore(4.2)
                .releaseDate(LocalDate.now().minusDays(60))
                .thumbnailUrl("http://example.com/game2.jpg")
                .build();

        List<GameCardResponse> content = List.of(testCardResponse1, testCardResponse2);
        testPage = new PageImpl<>(content, PageRequest.of(0, 10), 2);
    }

    @Test
    @DisplayName("파라미터 없이 호출하면 Service에 null이 전달되어야 한다")
    void getPopular_WithoutParameters_ShouldPassNullToService() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), isNull(), isNull()))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("인기 게임 1"));

        // verify
        verify(gameService).getPopular(any(Pageable.class), isNull(), isNull());
    }

    @Test
    @DisplayName("minReviews 파라미터가 올바르게 전달되어야 한다")
    void getPopular_WithMinReviews_ShouldPassCorrectValue() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), eq(5), isNull()))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("minReviews", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // verify
        verify(gameService).getPopular(any(Pageable.class), eq(5), isNull());
    }

    @Test
    @DisplayName("minAverageScore 파라미터가 올바르게 전달되어야 한다")
    void getPopular_WithMinAverageScore_ShouldPassCorrectValue() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), isNull(), eq(4.0)))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("minAverageScore", "4.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // verify
        verify(gameService).getPopular(any(Pageable.class), isNull(), eq(4.0));
    }

    @Test
    @DisplayName("여러 파라미터를 동시에 전달할 수 있어야 한다")
    void getPopular_WithMultipleParameters_ShouldPassAllValues() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), eq(10), eq(4.5)))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("minReviews", "10")
                        .param("minAverageScore", "4.5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // verify
        verify(gameService).getPopular(any(Pageable.class), eq(10), eq(4.5));
    }

    @Test
    @DisplayName("페이징 파라미터가 올바르게 처리되어야 한다")
    void getPopular_WithPagingParameters_ShouldHandleCorrectly() throws Exception {
        // given
        Page<GameCardResponse> customPage = new PageImpl<>(
                List.of(testCardResponse1),
                PageRequest.of(1, 5),
                10
        );
        given(gameService.getPopular(any(Pageable.class), isNull(), isNull()))
                .willReturn(customPage);

        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("응답 JSON 구조가 올바르게 형성되어야 한다")
    void getPopular_ShouldReturnCorrectJsonStructure() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), isNull(), isNull()))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].price").exists())
                .andExpect(jsonPath("$.content[0].discountRate").exists())
                .andExpect(jsonPath("$.content[0].tags").isArray())
                .andExpect(jsonPath("$.content[0].publisherId").exists())
                .andExpect(jsonPath("$.content[0].publisherName").exists())
                .andExpect(jsonPath("$.content[0].reviewCount").exists())
                .andExpect(jsonPath("$.content[0].averageScore").exists())
                .andExpect(jsonPath("$.content[0].releaseDate").exists())
                .andExpect(jsonPath("$.content[0].thumbnailUrl").exists());
    }

    @Test
    @DisplayName("빈 결과도 올바르게 반환되어야 한다")
    void getPopular_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // given
        Page<GameCardResponse> emptyPage = Page.empty(PageRequest.of(0, 10));
        given(gameService.getPopular(any(Pageable.class), isNull(), isNull()))
                .willReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/games/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("잘못된 타입의 파라미터는 400 에러를 반환해야 한다")
    void getPopular_WithInvalidParameterType_ShouldReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("minReviews", "not-a-number"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/games/popular")
                        .param("minAverageScore", "invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기본 페이지 크기가 10으로 설정되어야 한다")
    void getPopular_WithoutSizeParam_ShouldUseDefaultSize() throws Exception {
        // given
        Page<GameCardResponse> defaultSizePage = new PageImpl<>(
                List.of(testCardResponse1, testCardResponse2),
                PageRequest.of(0, 10),
                2
        );
        given(gameService.getPopular(argThat(pageable ->
                pageable.getPageSize() == 10), isNull(), isNull()))
                .willReturn(defaultSizePage);

        // when & then
        mockMvc.perform(get("/api/games/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("정렬 파라미터가 있어도 무시되어야 한다 (인기순은 이미 정렬됨)")
    void getPopular_WithSortParameter_ShouldIgnoreSort() throws Exception {
        // given
        given(gameService.getPopular(any(Pageable.class), isNull(), isNull()))
                .willReturn(testPage);

        // when & then
        mockMvc.perform(get("/api/games/popular")
                        .param("sort", "name,asc"))  // 정렬 파라미터 추가
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // Service는 정렬 관계없이 호출됨 (Repository에서 이미 ORDER BY 처리)
        verify(gameService).getPopular(any(Pageable.class), isNull(), isNull());
    }
}