package com.imfine.ngs.game.tag;

import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.tag.GameTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link GameTagRepository} 테스트 클래스.
 * GameTag 엔티티의 CRUD 동작을 검증합니다.
 *
 * @author chan
 */
@DataJpaTest
class GameTagRepositoryTest {

    @Autowired
    private GameTagRepository gameTagRepository;

    private GameTag gameTag;

    @BeforeEach
    void setUp() {
        gameTag = new GameTag();
        // Setter가 없으므로 Reflection 사용
        setField(gameTag, "tagType", GameTagType.ACTION);
    }

    @Test
    @DisplayName("GameTag 저장 및 조회 테스트")
    void saveAndFindGameTag() {
        // given
        GameTag savedTag = gameTagRepository.save(gameTag);

        // when
        Optional<GameTag> foundTag = gameTagRepository.findById(savedTag.getId());

        // then
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getTagType()).isEqualTo(GameTagType.ACTION);
    }

    @Test
    @DisplayName("GameTagType으로 GameTag 조회 테스트")
    void findByTagType() {
        // given
        gameTagRepository.save(gameTag);

        // when
        Optional<GameTag> foundTag = gameTagRepository.findByTagType(GameTagType.ACTION);

        // then
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getTagType()).isEqualTo(GameTagType.ACTION);
    }

    @Test
    @DisplayName("GameTagType 존재 여부 확인 테스트")
    void existsByTagType() {
        // given
        gameTagRepository.save(gameTag);

        // when
        boolean exists = gameTagRepository.existsByTagType(GameTagType.ACTION);
        boolean notExists = gameTagRepository.existsByTagType(GameTagType.RPG);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("모든 GameTagType에 대한 저장 테스트")
    void saveAllGameTagTypes() {
        // given & when
        for (GameTagType tagType : GameTagType.values()) {
            GameTag tag = new GameTag();
            setField(tag, "tagType", tagType);
            gameTagRepository.save(tag);
        }

        // then
        long count = gameTagRepository.count();
        assertThat(count).isEqualTo(GameTagType.values().length);
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