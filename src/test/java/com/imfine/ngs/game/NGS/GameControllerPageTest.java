package com.imfine.ngs.game.NGS;

import com.imfine.ngs._global.config.security.jwt.JwtAuthenticationFilter;
import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.game.controller.search.GameSearchController;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.service.search.GameSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 게임 컨트롤러 페이지 기능 테스트 클래스.
 * Page 기반의 응답을 검증합니다.
 *
 * @author chan
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(GameSearchController.class)
public class GameControllerPageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameSearchService gameSearchService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Game testGame;
    private List<Game> testGames;
    private Page<Game> testPage;

    @BeforeEach
    void setUp() {
        // 테스트 게임 데이터 생성
        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .price(50000L)
                .tag("Action")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        Game testGame2 = Game.builder()
                .id(2L)
                .name("Test Game 2")
                .price(30000L)
                .tag("RPG")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        Game testGame3 = Game.builder()
                .id(3L)
                .name("Test Game 3")
                .price(40000L)
                .tag("Strategy")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testGames = Arrays.asList(testGame, testGame2, testGame3);
    }

    @DisplayName("게임 전체 조회 - Page 응답 검증")
    @Test
    void findAllGames_WithPageResponse() throws Exception {
        // given
        Page<Game> gamePage = new PageImpl<>(testGames, PageRequest.of(0, 100), testGames.size());
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(gamePage);

        // when & then
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                // Page 메타데이터 검증
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Game"))
                .andExpect(jsonPath("$.content[0].price").value(50000L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[2].id").value(3L))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @DisplayName("게임 전체 조회 - 정렬 파라미터 검증")
    @Test
    void findAllGames_WithSortParameter() throws Exception {
        // given
        Page<Game> gamePage = new PageImpl<>(testGames, PageRequest.of(0, 100), testGames.size());
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(gamePage);

        // when & then - 가격 내림차순 정렬
        mockMvc.perform(get("/api/games")
                        .param("sort", "PRICE_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3));

        // when & then - 이름 오름차순 정렬 (기본값)
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("게임 전체 조회 - 빈 결과 Page 응답")
    @Test
    void findAllGames_EmptyPage() throws Exception {
        // given
        Page<Game> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 100), 0);
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @DisplayName("게임 전체 조회 - 페이지네이션 메타데이터 검증")
    @Test
    void findAllGames_PaginationMetadata() throws Exception {
        // given - 총 15개 데이터, 5개씩 페이징, 2페이지 (0-indexed)
        List<Game> pageGames = new ArrayList<>();
        for (int i = 11; i <= 15; i++) {
            pageGames.add(Game.builder()
                    .id((long) i)
                    .name("Game " + i)
                    .price(10000L * i)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        Page<Game> thirdPage = new PageImpl<>(pageGames, PageRequest.of(2, 5), 15);
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(thirdPage);

        // when & then
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(5));
    }

    @DisplayName("게임 단일 조회 성공 - 활성 게임")
    @Test
    void findGameById_Success() throws Exception {
        // given
        when(gameSearchService.findActiveById(1L)).thenReturn(testGame);

        // when & then
        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Game"))
                .andExpect(jsonPath("$.price").value(50000L))
                .andExpect(jsonPath("$.tag").value("Action"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @DisplayName("게임 단일 조회 실패 - 존재하지 않는 게임")
    @Test
    void findGameById_NotFound() throws Exception {
        // given
        when(gameSearchService.findActiveById(999L))
                .thenThrow(new RuntimeException("Game not found with id: 999"));

        // when & then
        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("게임 전체 조회 - 다양한 정렬 옵션 테스트")
    @Test
    void findAllGames_VariousSortOptions() throws Exception {
        // given
        Page<Game> gamePage = new PageImpl<>(testGames, PageRequest.of(0, 100), testGames.size());
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(gamePage);

        // when & then - 날짜 내림차순
        mockMvc.perform(get("/api/games")
                        .param("sort", "DATE_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // when & then - 이름 내림차순
        mockMvc.perform(get("/api/games")
                        .param("sort", "NAME_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // when & then - 가격 오름차순
        mockMvc.perform(get("/api/games")
                        .param("sort", "PRICE_ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("게임 전체 조회 - 큰 데이터셋의 페이지네이션")
    @Test
    void findAllGames_LargeDatasetPagination() throws Exception {
        // given - 100개 중 첫 10개만 반환하는 페이지
        List<Game> firstTenGames = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            firstTenGames.add(Game.builder()
                    .id((long) i)
                    .name("Game " + i)
                    .price(10000L * i)
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .build());
        }

        Page<Game> firstPage = new PageImpl<>(firstTenGames, PageRequest.of(0, 10), 100);
        when(gameSearchService.findAll(anyInt(), anyInt(), any(SortType.class))).thenReturn(firstPage);

        // when & then
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.totalPages").value(10))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.numberOfElements").value(10));
    }
}