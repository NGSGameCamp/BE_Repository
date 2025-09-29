package com.imfine.ngs.game.tag;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.entity.tag.util.LinkedTagId;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.repository.tag.GameTagRepository;
import com.imfine.ngs.game.repository.tag.LinkedTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link LinkedTagRepository} 테스트 클래스.
 * Game과 GameTag 간의 다대다 관계 매핑을 검증합니다.
 *
 * @author chan
 */
@DataJpaTest
class LinkedTagRepositoryTest {

    @Autowired
    private LinkedTagRepository linkedTagRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameTagRepository gameTagRepository;

    private Game game;
    private GameTag actionTag;
    private GameTag rpgTag;

    @BeforeEach
    void setUp() {
        // Game 생성 및 저장
        game = Game.builder()
                .name("테스트 게임")
                .price(10000L)
                .gameStatus(GameStatusType.ACTIVE)
                .build();
        game = gameRepository.save(game);

        // GameTag 생성 및 저장
        actionTag = createAndSaveTag(GameTagType.ACTION);
        rpgTag = createAndSaveTag(GameTagType.RPG);
    }

    @Test
    @DisplayName("게임 ID로 LinkedTag 조회 테스트")
    void findByGameId() {
        // given
        createLinkedTag(game, actionTag);
        createLinkedTag(game, rpgTag);

        // when
        List<LinkedTag> linkedTags = linkedTagRepository.findByGame_Id(game.getId());

        // then
        assertThat(linkedTags).hasSize(2);
        assertThat(linkedTags)
                .extracting(lt -> lt.getGameTag().getTagType())
                .containsExactlyInAnyOrder(GameTagType.ACTION, GameTagType.RPG);
    }

    @Test
    @DisplayName("LinkedTag 저장 및 조회 테스트")
    void saveAndFindLinkedTag() {
        // given
        LinkedTag linkedTag = createLinkedTag(game, actionTag);

        // when
        LinkedTag savedLinkedTag = linkedTagRepository.save(linkedTag);

        // then
        assertThat(savedLinkedTag).isNotNull();
        assertThat(savedLinkedTag.getId()).isNotNull();
        assertThat(savedLinkedTag.getGame().getId()).isEqualTo(game.getId());
        assertThat(savedLinkedTag.getGameTag().getTagType()).isEqualTo(GameTagType.ACTION);
    }

    @Test
    @DisplayName("게임에 여러 태그 연결 테스트")
    void linkMultipleTagsToGame() {
        // given
        GameTag strategyTag = createAndSaveTag(GameTagType.STRATEGY);
        GameTag puzzleTag = createAndSaveTag(GameTagType.PUZZLE);

        // when
        createLinkedTag(game, actionTag);
        createLinkedTag(game, rpgTag);
        createLinkedTag(game, strategyTag);
        createLinkedTag(game, puzzleTag);

        // then
        List<LinkedTag> linkedTags = linkedTagRepository.findByGame_Id(game.getId());
        assertThat(linkedTags).hasSize(4);
        assertThat(linkedTags)
                .extracting(lt -> lt.getGameTag().getTagType())
                .containsExactlyInAnyOrder(
                    GameTagType.ACTION,
                    GameTagType.RPG,
                    GameTagType.STRATEGY,
                    GameTagType.PUZZLE
                );
    }

    @Test
    @DisplayName("LinkedTag 삭제 테스트")
    void deleteLinkedTag() {
        // given
        LinkedTag linkedTag = createLinkedTag(game, actionTag);
        linkedTagRepository.save(linkedTag);

        // when
        linkedTagRepository.delete(linkedTag);

        // then
        List<LinkedTag> remainingTags = linkedTagRepository.findByGame_Id(game.getId());
        assertThat(remainingTags).isEmpty();
    }

    /**
     * GameTag 생성 및 저장 헬퍼 메서드
     */
    private GameTag createAndSaveTag(GameTagType tagType) {
        GameTag tag = new GameTag();
        setField(tag, "tagType", tagType);
        return gameTagRepository.save(tag);
    }

    /**
     * LinkedTag 생성 헬퍼 메서드
     */
    private LinkedTag createLinkedTag(Game game, GameTag tag) {
        LinkedTag linkedTag = new LinkedTag();
        LinkedTagId id = new LinkedTagId();

        // LinkedTagId 설정
        setField(id, "gameId", game.getId());
        setField(id, "tagId", tag.getId());

        // LinkedTag 설정
        setField(linkedTag, "id", id);
        setField(linkedTag, "game", game);
        setField(linkedTag, "gameTag", tag);

        return linkedTagRepository.save(linkedTag);
    }

    /**
     * Reflection을 사용한 필드 설정 헬퍼 메서드
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}