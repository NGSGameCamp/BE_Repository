package com.imfine.ngs.game.NGS;

import com.imfine.ngs.game.controller.NGSController;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.service.MainPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.support.WebContentGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NGS 메인 화면 호출 테스트
 * 현재는 새로운 게임 , 추천 게임, 인기 게임, 할인 중인 게임으로 테스트한다.
 *
 * @author chan
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NGSController.class)
public class NGSControllerTest {

    // mock
    @Autowired
    private MockMvc mockMvc;

    // MainPageService
    @MockitoBean
    private MainPageService mainPageService;

    // mockMainPageData;
    private Map<String, Object> mockMainPageData;
    // List<Game> testGames
    List<Game> testGame = createTestGames();

    @Autowired
    private WebContentGenerator webContentGenerator;

    // setup
    @BeforeEach
    void setUp() {

        testGame = createTestGames();

        mockMainPageData = new HashMap<>();
        mockMainPageData.put("newGames", testGame.subList(0, 2));
        mockMainPageData.put("recommendedGames", testGame.subList(2, 4));
        mockMainPageData.put("popularGames", testGame.subList(0, 3));
        mockMainPageData.put("discountGames", testGame.subList(1, 3));
    }

    private List<Game> createTestGames() {
        List<Game> testGames = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            Game game = Game.builder()
                    .id((long) i)
                    .name("테스트 게임" + i)
                    .price(10000L * i)
                    .tag("RPG")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            testGames.add(game);
        }


        return testGames;
    }

    // 메인페이지 조회
    @DisplayName("메인페이지 조회")
    @Test
    void getMainPage_WithDefaultValue() throws Exception {

        // given
        // mainServicePage.findAll() -> return testGameData
        when(mainPageService.findAllMainPageGameData()).thenReturn(mockMainPageData);

        // when & then
        // api 확인
        mockMvc.perform(get("/api/main"))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.newGames").isArray())
                .andExpect(jsonPath("$.recommendedGames").isArray())
                .andExpect(jsonPath("$.popularGames").isArray())
                .andExpect(jsonPath("$.discountGames").isArray())
                .andExpect(jsonPath("$.newGames.length()").value(2))
                .andExpect(jsonPath("$.recommendedGames.length()").value(2))
                .andExpect(jsonPath("$.popularGames.length()").value(3))
                .andExpect(jsonPath("$.discountGames.length()").value(2));
    }

    // 메인 페이지 - 커스텀 파라미터

    // 메인 페이지 조회 - 게임 상세 정보 확인

    // 메인 페이지 조회 - 빈 결과 처리

    // 메인 페이지 조회 - 서비스 예외 처리


}
