package com.imfine.ngs.game;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.env.util.LinkedEnvId;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.repository.env.EnvRepository;
import com.imfine.ngs.game.repository.env.LinkedEnvRepository;
import com.imfine.ngs.game.service.search.GameSearchService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Page를 사용한 게임 검색 테스트 클래스
 * 페이지네이션, 정렬, 검색 조건을 종합적으로 테스트
 *
 * @author chan
 */
@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class GameSearchPageTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameSearchService gameSearchService;

    @Autowired
    private LinkedEnvRepository linkedEnvRepository;

    @Autowired
    private EnvRepository envRepository;

    private Env savedMac;
    private Env savedWindows;
    private Env savedLinux;

    @BeforeEach
    void setUp() {
        // 환경 설정
        Env macEnv = new Env();
        macEnv.setEnvType(EnvType.MAC);
        savedMac = envRepository.save(macEnv);

        Env windowsEnv = new Env();
        windowsEnv.setEnvType(EnvType.WINDOWS);
        savedWindows = envRepository.save(windowsEnv);

        Env linuxEnv = new Env();
        linuxEnv.setEnvType(EnvType.LINUX);
        savedLinux = envRepository.save(linuxEnv);

        // 테스트 게임 데이터 생성 - 총 30개
        // Mac 게임 15개 (Active)
        for (int i = 0; i < 15; i++) {
            Game game = Game.builder()
                    .name("MacGame" + i)
                    .price(10000L + i * 1000)
                    .tag("Action")
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(30 - i))
                    .build();

            Game saved = gameRepository.save(game);
            createLinkedEnv(saved, savedMac);
        }

        // Windows 게임 10개 (5개 Active, 5개 Inactive)
        for (int i = 0; i < 10; i++) {
            Game game = Game.builder()
                    .name("WinGame" + i)
                    .price(20000L + i * 1000)
                    .tag("RPG")
                    .isActive(i < 5)
                    .createdAt(LocalDateTime.now().minusDays(20 - i))
                    .build();

            Game saved = gameRepository.save(game);
            createLinkedEnv(saved, savedWindows);
        }

        // Linux 게임 5개 (Active)
        for (int i = 0; i < 5; i++) {
            Game game = Game.builder()
                    .name("LinuxGame" + i)
                    .price(30000L + i * 1000)
                    .tag("Strategy")
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(10 - i))
                    .build();

            Game saved = gameRepository.save(game);
            createLinkedEnv(saved, savedLinux);
        }
    }

    private void createLinkedEnv(Game game, Env env) {
        LinkedEnv linkedEnv = new LinkedEnv();
        LinkedEnvId id = new LinkedEnvId();
        id.setGameId(game.getId());
        id.setEnvId(env.getId());
        linkedEnv.setId(id);
        linkedEnv.setGame(game);
        linkedEnv.setEnv(env);
        linkedEnvRepository.save(linkedEnv);
    }

    // ============= 기본 페이지네이션 테스트 =============

    @DisplayName("첫 페이지 조회 테스트")
    @Test
    void testFirstPage() {
        // when - 첫 페이지, 10개씩
        Page<Game> firstPage = gameSearchService.findAll(0, 10, SortType.NAME_ASC);

        // then
        assertThat(firstPage).isNotNull();
        assertThat(firstPage.getNumber()).isEqualTo(0);  // 현재 페이지 번호
        assertThat(firstPage.getSize()).isEqualTo(10);  // 페이지 크기
        assertThat(firstPage.getNumberOfElements()).isEqualTo(10);  // 현재 페이지의 요소 개수
        assertThat(firstPage.getTotalElements()).isEqualTo(25L);  // 전체 활성 게임 개수
        assertThat(firstPage.getTotalPages()).isEqualTo(3);  // 전체 페이지 수
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.isLast()).isFalse();
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(firstPage.hasPrevious()).isFalse();
    }

    @DisplayName("마지막 페이지 조회 테스트")
    @Test
    void testLastPage() {
        // when - 마지막 페이지 (3번째 페이지, 0-indexed이므로 2)
        Page<Game> lastPage = gameSearchService.findAll(2, 5, SortType.NAME_ASC);

        // then
        assertThat(lastPage.getNumber()).isEqualTo(2);
        assertThat(lastPage.getNumberOfElements()).isEqualTo(5);  // 마지막 페이지는 5개만
        assertThat(lastPage.isFirst()).isFalse();
        assertThat(lastPage.isLast()).isTrue();
        assertThat(lastPage.hasNext()).isFalse();
        assertThat(lastPage.hasPrevious()).isTrue();
    }

    @DisplayName("다양한 페이지 크기 테스트")
    @Test
    void testDifferentPageSizes() {
        // 5개씩
        Page<Game> page5 = gameSearchService.findAll(0, 5, SortType.NAME_ASC);
        assertThat(page5.getTotalPages()).isEqualTo(5);  // 25 / 5 = 5 페이지

        // 20개씩
        Page<Game> page20 = gameSearchService.findAll(0, 20, SortType.NAME_ASC);
        assertThat(page20.getTotalPages()).isEqualTo(2);  // 25 / 20 = 2 페이지

        // 50개씩 (전체보다 큰 경우)
        Page<Game> page50 = gameSearchService.findAll(0, 50, SortType.NAME_ASC);
        assertThat(page50.getTotalPages()).isEqualTo(1);  // 한 페이지에 모두
        assertThat(page50.getNumberOfElements()).isEqualTo(25);
    }

    // ============= 정렬 + 페이지네이션 테스트 =============

    @DisplayName("이름순 정렬 페이징 테스트")
    @Test
    void testSortByNameWithPaging() {
        // when - 이름 오름차순
        Page<Game> pageAsc = gameSearchService.findAll(0, 5, SortType.NAME_ASC);

        // then
        List<Game> games = pageAsc.getContent();
        assertThat(games).hasSize(5);
        assertThat(games.get(0).getName()).isLessThan(games.get(4).getName());

        // 이름 내림차순
        Page<Game> pageDesc = gameSearchService.findAll(0, 5, SortType.NAME_DESC);
        List<Game> gamesDesc = pageDesc.getContent();
        assertThat(gamesDesc.get(0).getName()).isGreaterThan(gamesDesc.get(4).getName());
    }

    @DisplayName("가격순 정렬 페이징 테스트")
    @Test
    void testSortByPriceWithPaging() {
        // when - 가격 오름차순
        Page<Game> pageAsc = gameSearchService.findAll(0, 10, SortType.PRICE_ASC);

        // then
        List<Game> games = pageAsc.getContent();
        assertThat(games).hasSize(10);
        // 첫 번째 게임이 가장 저렴
        assertTrue(games.get(0).getPrice() <= games.get(9).getPrice());

        // 가격 내림차순
        Page<Game> pageDesc = gameSearchService.findAll(0, 10, SortType.PRICE_DESC);
        List<Game> gamesDesc = pageDesc.getContent();
        assertTrue(gamesDesc.get(0).getPrice() >= gamesDesc.get(9).getPrice());
    }

    @DisplayName("날짜순 정렬 페이징 테스트")
    @Test
    void testSortByDateWithPaging() {
        // when - 최신순
        Page<Game> pageLatest = gameSearchService.findAll(0, 5, SortType.DATE_DESC);

        // then
        List<Game> games = pageLatest.getContent();
        // Linux 게임들이 가장 최근에 생성됨
        assertTrue(games.stream().anyMatch(g -> g.getName().startsWith("Linux")));

        // 오래된순
        Page<Game> pageOldest = gameSearchService.findAll(0, 5, SortType.DATE_ASC);
        List<Game> oldGames = pageOldest.getContent();
        // Mac 게임들이 가장 오래됨
        assertTrue(oldGames.stream().anyMatch(g -> g.getName().startsWith("Mac")));
    }

    // ============= 조건별 검색 + 페이지네이션 테스트 =============

    @DisplayName("게임 이름으로 검색 + 페이징")
    @Test
    void testSearchByNameWithPaging() {
        // when - "Mac"이 포함된 게임 검색
        Page<Game> page = gameSearchService.findByGameName("Mac", 0, 5, SortType.NAME_ASC);

        // then
        assertThat(page.getTotalElements()).isEqualTo(15L);  // Mac 게임 15개
        assertThat(page.getTotalPages()).isEqualTo(3);  // 5개씩 3페이지
        assertThat(page.getContent()).allMatch(game -> game.getName().contains("Mac"));
    }

    @DisplayName("태그로 검색 + 페이징")
    @Test
    void testSearchByTagWithPaging() {
        // when - Action 태그 검색
        Page<Game> actionPage = gameSearchService.findByTag(0, 10, "Action", SortType.NAME_ASC);

        // then
        assertThat(actionPage.getTotalElements()).isEqualTo(15L);  // Action 게임 15개
        assertThat(actionPage.getContent()).allMatch(game -> "Action".equals(game.getTag()));

        // RPG 태그 검색 (일부만 활성)
        Page<Game> rpgPage = gameSearchService.findByTag(0, 10, "RPG", SortType.NAME_ASC);
        assertThat(rpgPage.getTotalElements()).isEqualTo(5L);  // 활성화된 RPG 게임 5개
    }

    @DisplayName("가격 범위로 검색 + 페이징")
    @Test
    void testSearchByPriceRangeWithPaging() {
        // when - 10000 ~ 20000 범위
        Page<Game> page = gameSearchService.findByPriceBetween(10000L, 20000L, 0, 10, SortType.PRICE_ASC);

        // then
        assertThat(page.getContent()).allMatch(game ->
                game.getPrice() >= 10000L && game.getPrice() <= 20000L
        );

        // 첫 페이지는 가격순으로 정렬됨
        List<Game> games = page.getContent();
        if (games.size() > 1) {
            assertTrue(games.get(0).getPrice() <= games.get(games.size() - 1).getPrice());
        }
    }

    @DisplayName("환경별 검색 + 페이징")
    @Test
    void testSearchByEnvWithPaging() {
        // when - Mac 환경
        Page<Game> macPage = gameSearchService.findByEnv(0, 5, EnvType.MAC, SortType.NAME_ASC);

        // then
        assertThat(macPage.getTotalElements()).isEqualTo(15L);
        assertThat(macPage.getTotalPages()).isEqualTo(3);

        // Windows 환경 (일부만 활성)
        Page<Game> winPage = gameSearchService.findByEnv(0, 10, EnvType.WINDOWS, SortType.NAME_ASC);
        assertThat(winPage.getTotalElements()).isEqualTo(5L);  // 활성화된 Windows 게임만

        // Linux 환경
        Page<Game> linuxPage = gameSearchService.findByEnv(0, 10, EnvType.LINUX, SortType.NAME_ASC);
        assertThat(linuxPage.getTotalElements()).isEqualTo(5L);
    }

    // ============= 엣지 케이스 테스트 =============

    @DisplayName("빈 결과 페이지 테스트")
    @Test
    void testEmptyPage() {
        // when - 존재하지 않는 태그로 검색
        Page<Game> emptyPage = gameSearchService.findByTag(0, 10, "NonExistent", SortType.NAME_ASC);

        // then
        assertThat(emptyPage.getContent()).isEmpty();
        assertThat(emptyPage.getTotalElements()).isEqualTo(0L);
        assertThat(emptyPage.getTotalPages()).isEqualTo(0);
        assertThat(emptyPage.isFirst()).isTrue();
        assertThat(emptyPage.isLast()).isTrue();
    }

    @DisplayName("페이지 범위 초과 요청 테스트")
    @Test
    void testOutOfRangePage() {
        // when - 존재하지 않는 페이지 요청 (100번째 페이지)
        Page<Game> outOfRangePage = gameSearchService.findAll(100, 10, SortType.NAME_ASC);

        // then
        assertThat(outOfRangePage.getContent()).isEmpty();
        assertThat(outOfRangePage.getNumber()).isEqualTo(100);
        assertThat(outOfRangePage.getTotalElements()).isEqualTo(25L);  // 전체 개수는 여전히 25
        assertThat(outOfRangePage.getTotalPages()).isEqualTo(3);  // 실제 페이지는 3개
    }

    @DisplayName("단일 페이지 결과 테스트")
    @Test
    void testSinglePageResult() {
        // when - Strategy 태그 (5개만 존재)
        Page<Game> singlePage = gameSearchService.findByTag(0, 10, "Strategy", SortType.NAME_ASC);

        // then
        assertThat(singlePage.getTotalElements()).isEqualTo(5L);
        assertThat(singlePage.getTotalPages()).isEqualTo(1);
        assertThat(singlePage.isFirst()).isTrue();
        assertThat(singlePage.isLast()).isTrue();
        assertThat(singlePage.hasNext()).isFalse();
        assertThat(singlePage.hasPrevious()).isFalse();
    }

    // ============= Repository 직접 테스트 =============

    @DisplayName("Repository 페이징 직접 테스트")
    @Test
    void testRepositoryPaging() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        // when
        Page<Game> page = gameRepository.findAllActive(pageable);

        // then
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(25L);

        // 모든 게임이 활성 상태인지 확인
        assertThat(page.getContent()).allMatch(Game::isActive);
    }

    @DisplayName("복잡한 정렬 조건 테스트")
    @Test
    void testComplexSorting() {
        // given - 가격 내림차순, 이름 오름차순으로 복합 정렬
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Order.desc("price"), Sort.Order.asc("name")));

        // when
        Page<Game> page = gameRepository.findAllActive(pageable);

        // then
        List<Game> games = page.getContent();
        assertThat(games).isNotEmpty();

        // 가격이 같은 경우 이름순으로 정렬되는지 확인
        for (int i = 0; i < games.size() - 1; i++) {
            Game current = games.get(i);
            Game next = games.get(i + 1);

            if (current.getPrice().equals(next.getPrice())) {
                assertThat(current.getName()).isLessThanOrEqualTo(next.getName());
            } else {
                assertThat(current.getPrice()).isGreaterThan(next.getPrice());
            }
        }
    }
}