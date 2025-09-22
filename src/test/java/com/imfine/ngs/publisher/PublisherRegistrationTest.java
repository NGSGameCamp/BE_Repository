package com.imfine.ngs.publisher;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.GameNotice;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author chan
 */
@ActiveProfiles("test")
@SpringBootTest
public class PublisherRegistrationTest {

    // 게임 공지 등록 서비스
    @Autowired
    NoticeRepository noticeRepository;

    // 등록할 리포지토리
    @Autowired
    GameRepository gameRepository;

    // 게임 등록 메서드
    @DisplayName("게임 등록 테스트")
    @Test
    public void registrationGame() {

        // given
        // 게임을 등록한다.
        Game testGame = Game.builder()
                .name("Zelda")
                .price(100000L)
                .env("Mac")
                .tag("Action")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        gameRepository.save(testGame);

        // when && then
        // 리포지토리가 null인가요?
        assertNotNull(testGame);

        // 저장소의 사이즈가 1인가요?
        assertEquals(1, gameRepository.count());

        // 리포지토리에 저장된 정보가 일치하는가요?
        assertEquals("Zelda", testGame.getName());
        assertEquals(100000L, testGame.getPrice());
        assertEquals("Mac", testGame.getEnv());
        assertEquals("Action", testGame.getTag());
        assertTrue(testGame.isActive());

    }

    // 잘못된 입력값 테스트
    @DisplayName("게임 등록 유효성 검사 (실패되어야한다)")
    @Test
    void registrationGameValidation() {

        // given
        // 잘못된 게임 정보 입력 테스트
        Game inValidGame = Game.builder()
                .name("")
                .price(-10000L)
                .env("Mac")
                .env("TestTag")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        // when && then
        // 객체가 null인가요?
        assertNotNull(inValidGame);
        // 게임 이름 비어있나요?
        assertTrue(inValidGame.getName().isBlank());
        // 게임 가격이 음수인가요?
        assertTrue(inValidGame.getPrice() >= 0);
        // 게임 사태가 활성화 되어있나요?
        assertTrue(inValidGame.isActive());
    }

    // 게임 공지 추가 테스트
    @DisplayName("등록된 게임에 공지를 추가한다.")
    @Test
    void registrationNotice() {

        // given
        // 등록된 게임
        // 게임 등록
        Game testGame = Game.builder()
                .name("Zelda")
                .price(10000L)
                .env("Mac")
                .tag("Action")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        Game saveGame = gameRepository.save(testGame);

        // 공지 생성
        GameNotice notice = GameNotice.builder()
                .game(testGame)
                .title("[업데이트] 젤다의 노래 패치노트")
                .content("버그를 해결했습니다.")
                .createdAt(LocalDateTime.now())
                .build();

        GameNotice savedNotice = noticeRepository.save(notice);

        // when

        // then
        // 공지 객체가 null인가요?
        assertThat(savedNotice).isNotNull();

        // 현재 작성하려는 공지가 작성하려는 게임의 아이디인가요?
        assertThat(savedNotice.getGame().getId()).isEqualTo(testGame.getId());

        // 공지 객체 이름이 비어있나요?
        assertThat(savedNotice.getTitle()).isNotBlank().isEqualTo("[업데이트] 젤다의 노래 패치노트");

        // 공지 내용이 유효한가요?
        assertThat(savedNotice.getContent()).isNotBlank().isEqualTo("버그를 해결했습니다.");

        // 등록된 객체가 예상 데이터와 같나요?
        assertThat(savedNotice)
                .satisfies(n -> {
                    assertThat(n.getId()).isNotNull();
                    assertThat(n.getTitle()).isEqualTo("[업데이트] 젤다의 노래 패치노트");
                    assertThat(n.getContent()).isEqualTo("버그를 해결했습니다.");
                    assertThat(n.getCreatedAt()).isNotNull();
                });


        // 저장소에 조회 확인
        // 저장소에 객체의 개수가 1인가요?
        assertThat(noticeRepository.count()).isEqualTo(1);

        // 저장된 객체의 속성이 예상과 같은가요?
        assertThat(noticeRepository.findByGameId(saveGame.getId()))
                .hasSize(1)
                .first()
                .extracting(GameNotice::getTitle)
                .isEqualTo("[업데이트] 젤다의 노래 패치노트");
    }
}
