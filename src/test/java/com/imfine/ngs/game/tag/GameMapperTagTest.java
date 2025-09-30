/*
package com.imfine.ngs.game.tag;

import com.imfine.ngs.game.dto.mapper.GameMapper;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameTagType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
*/
/**
 * {@link GameMapper} 태그 변환 테스트 클래스.
 * LinkedTag를 GameTagResponse로 변환하는 매핑 로직을 검증합니다.
 *
 * 현재 GameMapper를 GameDetailMapper로 대체하여 주석 처리됨
 *
 * @author chan
 */
/*
class GameMapperTagTest {

    private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

    @Test
    @DisplayName("LinkedTag를 GameTagResponse로 변환 테스트")
    void toTagResponse() {
        // given
        GameTag gameTag = new GameTag();
        setField(gameTag, "tagType", GameTagType.ACTION);

        LinkedTag linkedTag = new LinkedTag();
        setField(linkedTag, "gameTag", gameTag);

        // when
        GameTagResponse response = gameMapper.toTagResponse(linkedTag);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTagType()).isEqualTo(GameTagType.ACTION);
        assertThat(response.getName()).isEqualTo("액션");
        assertThat(response.getDescription()).isEqualTo("빠른 반응과 실시간 전투가 특징인 게임");
    }

    @Test
    @DisplayName("GameTagType에서 한글 이름 추출 테스트")
    void getTagTypeName() {
        // when
        String actionName = gameMapper.getTagTypeName(GameTagType.ACTION);
        String rpgName = gameMapper.getTagTypeName(GameTagType.RPG);
        String strategyName = gameMapper.getTagTypeName(GameTagType.STRATEGY);

        // then
        assertThat(actionName).isEqualTo("액션");
        assertThat(rpgName).isEqualTo("롤플레잉");
        assertThat(strategyName).isEqualTo("전략");
    }

    @Test
    @DisplayName("GameTagType에서 설명 추출 테스트")
    void getTagTypeDescription() {
        // when
        String actionDesc = gameMapper.getTagTypeDescription(GameTagType.ACTION);
        String rpgDesc = gameMapper.getTagTypeDescription(GameTagType.RPG);
        String strategyDesc = gameMapper.getTagTypeDescription(GameTagType.STRATEGY);

        // then
        assertThat(actionDesc).isEqualTo("빠른 반응과 실시간 전투가 특징인 게임");
        assertThat(rpgDesc).isEqualTo("캐릭터 성장과 스토리가 중심인 게임");
        assertThat(strategyDesc).isEqualTo("전술과 계획이 중요한 게임");
    }

    @Test
    @DisplayName("null GameTagType 처리 테스트")
    void handleNullGameTagType() {
        // when
        String name = gameMapper.getTagTypeName(null);
        String description = gameMapper.getTagTypeDescription(null);

        // then
        assertThat(name).isNull();
        assertThat(description).isNull();
    }

    @Test
    @DisplayName("LinkedTag Set을 GameTagResponse Set으로 변환 테스트")
    void mapLinkedTags() {
        // given
        Set<LinkedTag> linkedTags = new HashSet<>();

        // ACTION 태그
        GameTag actionTag = new GameTag();
        setField(actionTag, "tagType", GameTagType.ACTION);
        LinkedTag actionLinkedTag = new LinkedTag();
        setField(actionLinkedTag, "gameTag", actionTag);
        linkedTags.add(actionLinkedTag);

        // RPG 태그
        GameTag rpgTag = new GameTag();
        setField(rpgTag, "tagType", GameTagType.RPG);
        LinkedTag rpgLinkedTag = new LinkedTag();
        setField(rpgLinkedTag, "gameTag", rpgTag);
        linkedTags.add(rpgLinkedTag);

        // when
        Set<GameTagResponse> responses = gameMapper.mapLinkedTags(linkedTags);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(GameTagResponse::getTagType)
                .containsExactlyInAnyOrder(GameTagType.ACTION, GameTagType.RPG);
        assertThat(responses)
                .extracting(GameTagResponse::getName)
                .containsExactlyInAnyOrder("액션", "롤플레잉");
    }

    @Test
    @DisplayName("빈 LinkedTag Set 변환 테스트")
    void mapEmptyLinkedTags() {
        // given
        Set<LinkedTag> emptyTags = new HashSet<>();

        // when
        Set<GameTagResponse> responses = gameMapper.mapLinkedTags(emptyTags);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("null LinkedTag Set 변환 테스트")
    void mapNullLinkedTags() {
        // when
        Set<GameTagResponse> responses = gameMapper.mapLinkedTags(null);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("모든 GameTagType 변환 테스트")
    void convertAllGameTagTypes() {
        // given & when & then
        for (GameTagType tagType : GameTagType.values()) {
            GameTag gameTag = new GameTag();
            setField(gameTag, "tagType", tagType);

            LinkedTag linkedTag = new LinkedTag();
            setField(linkedTag, "gameTag", gameTag);

            GameTagResponse response = gameMapper.toTagResponse(linkedTag);

            assertThat(response.getTagType()).isEqualTo(tagType);
            assertThat(response.getName()).isNotNull();
            assertThat(response.getDescription()).isNotNull();
        }
    }

    /**
     * Reflection을 사용한 필드 설정 헬퍼 메서드
     */
//    private void setField(Object target, String fieldName, Object value) {
//        try {
//            var field = target.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            field.set(target, value);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to set field: " + fieldName, e);
//        }
//    }
// }
// */