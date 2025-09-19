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
import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 게임 조회 테스트 클래스.
 * 메인페이지에서 게임 조회
 * 조견별 게임 조회 테스트
 *
 * @author chan
 */
@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class GameSearchTest {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameSearchService gameSearchService;
    @Autowired
    private LinkedEnvRepository linkedEnvRepository;
    @Autowired
    private EnvRepository envRepository;

    // 게임을 생성하는 테스트 코드
    @BeforeEach
    void dataInitializer() {

        Env macEnv = new Env();
        macEnv.setEnvType(EnvType.MAC);
        Env savedMac = envRepository.save(macEnv);

        Env windowsEnv = new Env();
        windowsEnv.setEnvType(EnvType.WINDOWS);
        Env savedWindows = envRepository.save(windowsEnv);

        Env linuxEnv = new Env();
        linuxEnv.setEnvType(EnvType.LINUX);
        Env savedLinux = envRepository.save(linuxEnv);

        for (int i = 0; i < 10; i++) {
            Game macGame = Game.builder()
                    .name("TestGame" + i)
                    .price(10000L + i)
                    .tag("Action")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Game saved = gameRepository.save(macGame);

            // linkedEvn 생성
            LinkedEnv linkedEnv = new LinkedEnv();
            LinkedEnvId id = new LinkedEnvId();

            linkedEnv.setId(id);
            id.setEnvId(macGame.getId());
            linkedEnv.setId(id);
            linkedEnv.setGame(saved);
            linkedEnv.setEnv(savedMac);

            linkedEnvRepository.save(linkedEnv);
        }

        for (int i = 10; i < 15; i++) {
            Game windGame = Game.builder()
                    .name("TestGame" + i)
                    .price(20000L + i)
                    .tag("RPG")
                    .isActive(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            Game saved = gameRepository.save(windGame);

            // env 적용
            LinkedEnv windowsLinkedEnv = new LinkedEnv();
            LinkedEnvId id = new LinkedEnvId();

            id.setGameId(saved.getId());
            id.setEnvId(windGame.getId());

            windowsLinkedEnv.setId(id);
            windowsLinkedEnv.setGame(saved);
            windowsLinkedEnv.setEnv(savedMac);

            linkedEnvRepository.save(windowsLinkedEnv);

        }

        for (int i = 15; i < 20; i++) {
            Game game = Game.builder()
                    .name("TestGame" + i)
                    .price(30000L + i)
                    .tag("RPG")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Game saved = gameRepository.save(game);

            LinkedEnv linuxLinkedEnv = new LinkedEnv();
            LinkedEnvId id = new LinkedEnvId();

            id.setGameId(saved.getId());
            id.setEnvId(linuxEnv.getId());

            linuxLinkedEnv.setId(id);
            linuxLinkedEnv.setGame(saved);
            linuxLinkedEnv.setEnv(savedLinux);

            linkedEnvRepository.save(linuxLinkedEnv);
        }
    }

    // isActive 필터링 테스트
    @DisplayName("isActive가 true인 게임만을 조회한다.")
    @Test
    void isActiveFiltering() {

        // given - BeforeEach에서 생성
        // Mac 게임 10개 (isActive = true)
        // Windows 게임 5개 (isActive = false)

        // when
        // isActive가 true인 게임만 조회
        Sort sort = Sort.by("name").ascending();
        List<Game> isActiveGames = gameRepository.findAllActive(sort);

        // then
        // null인가요?
        assertNotNull(isActiveGames);
        // 조회된 게임의 갯수가 일치하나요?;
        assertEquals(15, isActiveGames.size());
        // 조회한 게임의 상태가 전부 isActive인가요?
        assertTrue(isActiveGames.stream().allMatch(Game::isActive));
        // isActive가 false인 게임은 조회가 되나요?
        assertFalse(isActiveGames.stream().anyMatch(game -> "Window".equals(game.isActive())));

    }

    @DisplayName("게임 전체 조회 테스트")
    @Test
    void mainPageFindAllGame() {

        // Given
        // dataInitializer()

        // When
        // 서비스에서 게임을 가져와야한다.
        List<Game> result = gameRepository.findAll();

        // Then
        // null인가요?
        // 생성한 개수가 일치하는가요?
        assertNotNull(result);
        assertEquals(20, result.size());

        // 첫 번째 게임 검증
        Game firstGame = result.stream()
                .filter(game -> game.getName().equals("TestGame0"))
                .findFirst()
                .orElse(null);

        assertNotNull(firstGame);
        assertEquals("TestGame0", firstGame.getName());
        assertEquals(10000L, firstGame.getPrice());
        assertEquals("Mac", firstGame.getEnv());
        assertEquals("Action", firstGame.getTag());
        assertTrue(firstGame.isActive());
    }

    // 게임 아이디로 단일 조회
    @DisplayName("게임 아이디로 단일 조회")
    @Test
    void findByGameId() {

        // given
        // dataInitializer로 생성하였다.
        Long gameId = 1L;

        // when
        Optional<Game> singleGame = gameRepository.findById(gameId);

        // then
        // null인가요?
        assertNotNull(singleGame);

        // 조회한 객체의 이름이 일치하나요?
        assertThat(singleGame.get().getId()).isEqualTo(gameId);

        // 조회한 객체의 가격이 일치하나요?
        assertThat(singleGame.get().getPrice()).isEqualTo(10000L);

        // 상태가 isActive인가요?
        assertThat(singleGame.get().isActive()).isTrue();
    }

    @DisplayName("게임 이름으로 조회")
    @Test
    void findByGameName() {

        // given
        // 10개의 Mac게임을 생성했다.
        // 5개의 Windows 게임을 생성했다.
        // 5개의 Linux 게임을 생성했다.

        // when
        // Mac 게임 객체 생성
        List<Game> gameList = gameSearchService.findByGameName("TestGame0", SortType.NAME_ASC);

        // then
        // null인가요?
        assertNotNull(gameList);
        // 사이즈가 1인가요?
        assertEquals(1, gameList.size());
        // isActive 상태가 true이며 환경이 mac인가요?
        assertTrue(gameList.stream().allMatch(game -> game.isActive() && game.getName().equals("TestGame0") && game.getEnv().equals("Mac") && game.getTag().equals("Action")));
    }

    // 날짜별 조회 (오래된순 정렬 , 최신순 정렬)
    @DisplayName("날짜별 조회 테스트 (오래된순 정렬, 최신순 정렬")
    @Test
    void findByCreateAt() throws InterruptedException {

        // given
        Thread.sleep(1000);
        Game newGame1 = Game.builder()
                .name("NewGame1")
                .price(5000L)
                .tag("Action")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        gameRepository.save(newGame1);

        Thread.sleep(100);

        Game newGame2 = Game.builder()
                .name("NewGame2")
                .price(5000L)
                .tag("RPG")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        gameRepository.save(newGame2);

        // when
        // 오래된순 정렬
        List<Game> oldestFirst = gameSearchService.findByCreatedAt(SortType.DATE_ASC);
        // 최신순 정렬
        List<Game> newestFirst = gameSearchService.findByCreatedAt(SortType.DATE_DESC);

        // then
        // null 체크
        assertNotNull(oldestFirst);
        assertNotNull(newestFirst);

        // 사이즈 체크
        assertEquals(17, oldestFirst.size());
        assertEquals(17, newestFirst.size());

        // 오래된 순: 첫 번째는 TestGam0~19 중 하나여야 함
        // 시작이름이 TestGame으로 시작해야 한다.
        String firstOldest = oldestFirst.getFirst().getName();
        assertTrue(firstOldest.startsWith("TestGame"));

        // 오래된 순: 마지막은 NewGame2여야한다.
        assertEquals("NewGame2", oldestFirst.getLast().getName());

        // 최신 순: 첫 번째는 NewGame2여야 함
        // 시작이름이 NewGame으로 시작해야한다.
        assertEquals("NewGame2", newestFirst.getFirst().getName());

        // 최신 순: 마지막은 TestGame 중 하나여야 함
        String lastNewest = newestFirst.getLast().getName();
        assertTrue(lastNewest.startsWith("TestGame"));

        // 오래된 순의 첫 번째와 최신 순의 마지막은 같아야한다.
        assertEquals(oldestFirst.getFirst().getName(), newestFirst.getLast().getName());
    }

    // OS별 검색
    @DisplayName("게임 실행환경으로 조회")
    @Test
    void findByEnv() {

        // given
        // Mac 게임 10개 생성
        // Window 게임 5개 생성
        // Linux 게임 5개 생성

        // when
        List<Game> macGames = gameSearchService.findByEnv(EnvType.MAC, SortType.NAME_ASC);
        List<Game> windowGames = gameSearchService.findByEnv(EnvType.WINDOWS, SortType.NAME_ASC);
        List<Game> linuxGames = gameSearchService.findByEnv(EnvType.LINUX, SortType.NAME_ASC);


        for (int i = 0; i < macGames.size(); i++) {
            System.out.println("================" + macGames.get(i).getEnv() + "================");
        }

        for (int i = 0; i < windowGames.size(); i++) {
            System.out.println("================" + windowGames.get(i).getEnv() + "================");
        }

        for (int i = 0; i < linuxGames.size(); i++) {
            System.out.println("================" + linuxGames.get(i).getEnv() + "================");
        }

        // then
        // null인가요?
        assertNotNull(macGames);
        assertNotNull(windowGames);
        assertNotNull(linuxGames);

        // 저장된 사이즈가 일치하나요?
        assertEquals(10, macGames.size());
        assertEquals(0, windowGames.size());
        assertEquals(5, linuxGames.size());

        // 리스트의 모든 OS환경이 일치하나요?

        // MAC 환경 검증
        assertThat(macGames).allSatisfy(game -> {
            assertThat(game.isActive()).isTrue();

            List<LinkedEnv> envs = linkedEnvRepository.findByGame_Id(game.getId());
            assertThat(envs)
                    .isNotEmpty()
                    .anyMatch(le -> le.getEnv().getEnvType() == EnvType.MAC);
        });

        // Window 환경 검증
        assertThat(windowGames).allSatisfy(game -> {
            assertThat(game.isActive()).isTrue();

            List<LinkedEnv> envs = linkedEnvRepository.findByGame_Id(game.getId());
            assertThat(envs)
                    .isNotEmpty()
                    .anyMatch(le -> le.getEnv().getEnvType() == EnvType.WINDOWS);
        });

        // Linux 환경 검증
        assertThat(linuxGames).allSatisfy(game -> {

            // isActive 인가요?
            assertThat(game.isActive()).isTrue();

            // 데이터베이스에서 env 조회
            List<LinkedEnv> envs = linkedEnvRepository.findByGame_Id(game.getId());

            assertThat(envs)
                    .isNotEmpty()
                    .anyMatch(le -> le.getEnv().getEnvType() == EnvType.LINUX);
        });
    }

    // 태그별 검색
    @DisplayName("게임 태그로 검색")
    @Test
    void findByTags() {

        // given
        // Action 게임 10개 생성
        // RPG 게임 5개 생성

        // when
        // 태그 게임들을 불러온다.
        List<Game> actionGame = gameSearchService.findByTag("Action", SortType.NAME_ASC);
        List<Game> rpgGame = gameSearchService.findByTag("RPG", SortType.NAME_ASC);

        // then
        // null인가요?
        assertNotNull(actionGame);
        assertNotNull(rpgGame);

        // 사이즈가 일치하나요?
        assertEquals(10, actionGame.size());
        assertEquals(5, rpgGame.size());

        // 리스트의 모든 게임이 태그와 일치하나요?
        assertTrue(actionGame.stream().allMatch(game -> game.isActive() && "Action".equals(game.getTag())));
        assertTrue(rpgGame.stream().allMatch(game -> game.isActive() && "RPG".equals(game.getTag())));
    }

    // 가격별 검색
    @DisplayName("가격별 검색 테스트")
    @Test
    void findByPrice() {

        // given
        // beforeEach에서 설정하였다.

        // when
        // Mac과 Window 리스트 객체를 추가한다.
        List<Game> cheapGames = gameSearchService.findByPriceBetween(10000L, 10005L, SortType.PRICE_ASC);
        List<Game> expensiveGames = gameSearchService.findByPriceBetween(30015L, 30020L, SortType.PRICE_DESC);

        // then
        // null인가요?
        assertNotNull(cheapGames);
        assertNotNull(expensiveGames);

        // 싼 상품의 개수가 6개인가요?
        assertEquals(6, cheapGames.size());
        // 비싼 제품의 개수가 5개인가요?
        assertEquals(5, expensiveGames.size());

        // 싼 상품의 가격은 일치하는가요?
        assertTrue(cheapGames.stream().allMatch(game -> game.isActive() && game.getPrice() >= 10000L && game.getPrice() <= 10005L));

        // 비싼 상품의 가격은 일치하는가요?
        assertTrue(expensiveGames.stream().allMatch(game -> game.isActive() && game.getPrice() >= 30015L && game.getPrice() <= 30020L));
    }
}
