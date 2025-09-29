//package com.imfine.ngs.game;
//
//import com.imfine.ngs._global.config.security.jwt.JwtAuthenticationFilter;
//import com.imfine.ngs._global.config.security.jwt.JwtUtil;
//import com.imfine.ngs.game.controller.search.GameSearchController;
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.service.search.GameSearchService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.servlet.support.WebContentGenerator;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * 게임({@link Game} 컨트롤러 테스트 클래스.
// *
// * @author chan
// */
//@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(GameSearchController.class)
//public class GameControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    // 서비스
//    @MockitoBean
//    private GameSearchService searchService;
//
//    private Game testGame;
//    private List<Game> testGames;
//    @Autowired
//    private WebContentGenerator webContentGenerator;
//    @Autowired
//    private GameSearchService gameSearchService;
//
//    @MockitoBean
//    private JwtUtil jwtUtil;
//
//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    // setup
//    @BeforeEach
//    void setup() {
//
//        testGame = Game.builder()
//                .id(1L)
//                .name("Test Game")
//                .price(50000L)
//                .tag("Action")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        Game testGame2 = Game.builder()
//                .id(2L)
//                .name("Test Game2")
//                .price(30000L)
//                .tag("RPG")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        testGames = Arrays.asList(testGame, testGame2);
//    }
//
//    // 게임 전체 조회
//    @DisplayName("게임 전체 조회 테스트")
//    @Test
//    void finaAllGames() throws Exception {
//
//        // given
//        // 게임 서비스의 findAll을 호출하고 게임들을 반환한다.
//        when(gameSearchService.findAll(any())).thenReturn(testGames);
//
//        // when & then
//        // api를 가져온다.
//        mockMvc.perform(get("/api/games"))
//                // 상태를 체크한다.
//                .andExpect(status().isOk())
//                // json의 첫번째 id가 일치하는지 확인한다.
//                .andExpect(jsonPath("$[0].id").value(1L))
//                // json의 첫번째 게임 이름이 일치하는지 확인한다.
//                .andExpect(jsonPath("$[0].name").value("Test Game"))
//                // json의 첫번째 게임의 가격이 일치하는지 확인한다.
//                .andExpect(jsonPath("$[0].price").value(50000L))
//                // json의 두번째 게임의 id가 일치하는지 확인한다.
//                .andExpect(jsonPath("$[1].id").value(2L));
//
//    }
//
//    // 게임 아이디로 단일 조회
//    @DisplayName("게임 단일 조회 성공 테스트")
//    @Test
//    void findGameByIdSuccess() throws Exception {
//        // given
//        when(gameSearchService.findActiveById(1L)).thenReturn(testGame);
//
//        // when & then
//        mockMvc.perform(get("/api/games/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Game"))
//                .andExpect(jsonPath("$.price").value(50000L))
//                .andExpect(jsonPath("$.tag").value("Action"))
//                .andExpect(jsonPath("$.active").value(true));
//    }
//
//    // 게임 아이디로 단일 조회 실패
//    @DisplayName("존재하지 않는 게임 조회시 404 반환")
//    @Test
//    void findGameByIdNotFound() throws Exception {
//        // given
//        when(gameSearchService.findActiveById(999L))
//                .thenThrow(new IllegalArgumentException("Game not found with id: 999"));
//
//        // when & then
//        mockMvc.perform(get("/api/games/999"))
//                .andExpect(status().isNotFound());
//    }
//
//    // 게임 이름으로 조회
//
//    // 게임 태그로 조회
//
//    // 게임 범위 가격으로 조회
//
//    // 게임 환경으로 조회
//}
