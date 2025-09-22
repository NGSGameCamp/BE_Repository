package com.imfine.ngs.game.NGS;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.service.MainPageService;
import com.imfine.ngs.game.service.search.GameSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 메인 화면에 게임이 잘 조회되는지 {@link com.imfine.ngs.game.service.MainPageService}를 통해 테스트한다.
 *
 * @author chan
 */
@ExtendWith(SpringExtension.class)
class MainPageServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MainPageServiceTest.class);
    // 메인 화면 게임 조회 서비스
    @InjectMocks
    MainPageService mainPageService;

    // 게임 조회 서비스
    @Mock
    GameSearchService searchService;

    List<Game> testGames;

    // setUp
    @BeforeEach
    void setUp() {
        testGames = createTestGames();
    }

    // 테스트 데이터 생성 헬퍼 메서드
    private List<Game> createTestGames() {
        List<Game> games = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Game game = Game.builder()
                    .id((long) i)
                    .name("테스트게임" + i)
                    .price(10000L * i)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .isActive(true)
                    .build();
            games.add(game);
        }
        return games;
    }

    private List<Game> createMixedDateGames() {
        List<Game> games = new ArrayList<>();

        // 최근 게임 (1개월 이내)
        for (int i = 1; i <= 3; i++) {
            games.add(Game.builder()
                    .id((long) i)
                    .name("신작게임" + i)
                    .price(50000L)
                    .createdAt(LocalDateTime.now().minusDays(i * 10))
                    .isActive(true)
                    .build());
        }

        // 오래된 게임 (3개월 이상)
        for (int i = 4; i <= 6; i++) {
            games.add(Game.builder()
                    .id((long) i)
                    .name("구작게임" + i)
                    .price(30000L)
                    .createdAt(LocalDateTime.now().minusMonths(4))
                    .isActive(true)
                    .build());
        }

        return games;
    }

    // 메인페이지 전체 데이터 조회 - 성공
    @DisplayName("메인 페이지 전체 데이터 조회 - 성공")
    @Test
    void getMainPageData_success() {

        // given
        // 단일 조회용
        // 왜 when을 두번 given 줄까?
        when(searchService.findByCreatedAt(SortType.DATE_DESC)).thenReturn(testGames);
        // 메인 화면에 줄 전체 Map<String, Object>
        when(searchService.findAll(any(SortType.class))).thenReturn(testGames);

        // when
        // 메인 페이지의 value에 5개씩 Key가 저장되었는지 확인한다.
        Map<String, Object> gameList = mainPageService.findAllMainPageGameData(5, 5, 5, 5);

        // then
        // MainPageService 객체가 Null인가요?
        assertThat(gameList).isNotNull();
        // 조회한 객체의 요소가 예상과 같나요?
        assertThat(gameList).containsKeys("newGames", "recommendedGames", "popularGames", "discountGames");

        // 리스트에 담긴 키에 newGames가 들어있나요?
        List<Game> checkContain_newGames = (List<Game>) gameList.get("newGames");
        assertThat(checkContain_newGames).isNotNull();

        // mainPageService에 담긴 객체를 searchService를 이용해 조회할 수 있나요?
        // 이 코드는 무엇일까?
        verify(searchService, times(1)).findByCreatedAt(any(SortType.class));
        verify(searchService, times(3)).findAll(any((SortType.class)));
    }

    // 신작게임조회 - 3개월 이내 게임만 필터링
    @DisplayName("신작 게임 조회 - 3개월 이내 게임 필터링")
    @Test
    void newGames_FilterThreeMonthsGames() {

        // given
        List<Game> gameList = createMixedDateGames();
        when(searchService.findByCreatedAt(SortType.DATE_DESC)).thenReturn(gameList);

        // when
        List<Game> newGames = mainPageService.getNewGames(5);

        // then
        // 객체가 Null인가요?
        assertThat(newGames).isNotNull();
        // 객체의 사이즈가 3인가요?
        assertThat(newGames.size()).isEqualTo(3);
        // 게임의 내부 필드가 일치하나요?
        assertThat(newGames.getFirst().getName()).contains("신작게임");
        // 최근 3개월 이내에 생성되었나요?
        LocalDateTime threeMonths = LocalDateTime.now().minusMonths(3);
        newGames.forEach(game -> {
            assertThat(game.getCreatedAt()).isAfter(threeMonths);
        });

        // 구작 게임은 포함되지 않았나요?
        assertThat(newGames).noneMatch(game -> game.getName().contains("구작"));
    }

    // 추천게임조회 - 무작위 선택
    @DisplayName("추전 게임 조회 - 무작위 선택")
    @Test
    void recommendedGames_RandomSelections() {

        // given
        // service.findAll
        when(searchService.findAll(SortType.DATE_DESC)).thenReturn(testGames);

        // when
        // mainPage.recommendAt();
        List<Game> recommendGames = mainPageService.getRecommendGames(5);

        // then
        // 객체가 null인가요?
        assertThat(recommendGames).isNotNull();
        // 조회한 사이즈가 일치하는가요?
        assertThat(recommendGames.size()).isEqualTo(5);
        assertThat(recommendGames).allMatch(Objects::nonNull);
        assertThat(recommendGames).isSubsetOf(testGames);
    }

    // 추천게임조회 - 게임 수가 limit보다 적을 때
    @DisplayName("추천 게임 조회 - 게임 수가 limit 보다 적을 때")
    @Test
    void recommendedGames_LowerThanLimit() {

        // given
        // testGames의 값 중 3개를 가져온다.
        List<Game> findThreeGames = testGames.subList(0, 3);

        // search로 findAll한다.
        when(searchService.findAll(SortType.DATE_DESC)).thenReturn(findThreeGames);

        // when
        // mainService에서 getRecommend 메서드 호출
        List<Game> recommendGames = mainPageService.getRecommendGames(5);

        // then
        // 객체가 null 인가요?
        assertThat(recommendGames).isNotNull();
        // 객체의 사이즈가 3인가요?
        // recommend 게임의 객체가 testGames에도 포함되어 있나요?
        assertThat(recommendGames).containsExactlyElementsOf(findThreeGames);


    }

    // 인기게임조회 - 현재는 무작위
    @DisplayName("인기 게임 조회 - 현재는 무작위")
    @Test
    void popularGames_RandomSelections() {

        // given
        // searchService.findAll()
        when(searchService.findAll(SortType.NAME_ASC)).thenReturn(testGames);

        // when
        //  mainService.getPopularGames
        List<Game> popularGames = mainPageService.getPopularGames(5);

        // then
        // 객체가 null인가요?
        assertThat(popularGames).isNotNull();
        // 객체의 사이즈가 5인가요?
        assertThat(popularGames.size()).isEqualTo(5);
        // popularGames가 전부 notnull인가요?
        assertThat(popularGames).allMatch(Objects::nonNull);
        // 객체에 testGame가 들어있나요?
        assertThat(popularGames).isSubsetOf(testGames);


    }

    // 할인게임조회 - 현재는 무작위
    @DisplayName("할인 게임 조회 - 현재는 무작위로 담아 두었다.")
    @Test
    void disCountGames_RandomSelections() {

        // given
        // searchService.findAll
        when(searchService.findAll(SortType.NAME_ASC)).thenReturn(testGames);

        // when
        // mainService.getDisCount
        List<Game> disCountGames = mainPageService.getDiscountGames(5);

        // then
        // 객체가 null인가요?
        assertThat(disCountGames).isNotNull();
        // 객체의 사이즈가 일치하나요?
        assertThat(disCountGames.size()).isEqualTo(5);
        // disCountGames이 모두 Notnull인가요?
        assertThat(disCountGames).allMatch(Objects::nonNull);
        // testGame이 찾은 게임 리스트 안에 들어있나요?
        assertThat(disCountGames).isSubsetOf(testGames);
    }

    // 메인페이지 기본값 조회 - 각 세션 5개씩
    @DisplayName("메인 페이지 기본값 조회 - 각 조건 5개씩")
    @Test
    void mainPageGame_DefaultValues() {

        // given
        // searchService에서 날짜로 조회가 되나요?
        when(searchService.findByCreatedAt(SortType.DATE_DESC)).thenReturn(testGames);
        // searchService에서 전부 조회가 되나요?
        when(searchService.findByCreatedAt(any(SortType.class))).thenReturn(testGames);

        // when
        // mainPageService.getMainPageGameData();
        Map<String, Object> mainPageGameData = mainPageService.findAllMainPageGameData();

        // then
        // 객체가 null인가요?
        assertThat(mainPageGameData).isNotNull();
        // Map의 키가 담겨있나요?
        assertThat(mainPageGameData).containsKeys("newGames", "recommendedGames", "popularGames", "discountGames");
        // 사이즈가 일치하나요?
        assertThat(mainPageGameData.size()).isEqualTo(4);
    }
}