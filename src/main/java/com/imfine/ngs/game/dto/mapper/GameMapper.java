package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.dto.response.EnvironmentResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameTagType;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game Entity와 DTO 간의 변환을 담당하는 MapStruct 매퍼 인터페이스.
 * MapStruct가 컴파일 시점에 구현체를 자동 생성합니다.
 *
 * @author chan
 */
@Mapper(componentModel = "spring")
public interface GameMapper {

    /**
     * GameCreateRequest를 Game 엔티티로 변환
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "env", ignore = true)
    @Mapping(target = "gameStatus", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Game toEntity(GameCreateRequest request);


    /**
     * Game 엔티티를 GameSummaryResponse로 변환
     */
    @Mapping(source = "gameStatus", target = "gameStatus")
    @Mapping(target = "environmentCount", expression = "java(game.getEnv() != null ? game.getEnv().size() : 0)")
    @Mapping(target = "tagCount", expression = "java(game.getTags() != null ? game.getTags().size() : 0)")
    @Mapping(target = "tags", ignore = true)
    GameSummaryResponse toSummaryResponse(Game game);

    /**
     * Game 엔티티를 GameDetailResponse로 변환
     */
    @Mapping(source = "gameStatus", target = "gameStatus")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "environments", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    GameDetailResponse toDetailResponse(Game game);

    /**
     * LinkedEnv를 EnvironmentResponse로 변환
     */
    @Mapping(source = "env.id", target = "id")
    @Mapping(source = "env.envType", target = "name", qualifiedByName = "envTypeName")
    @Mapping(source = "env.envType", target = "type", qualifiedByName = "envTypeName")
    EnvironmentResponse toEnvironmentResponse(LinkedEnv linkedEnv);

    /**
     * Game 엔티티를 GameSummaryResponse로 완전 변환 (태그 포함)
     */
    @Mapping(source = "gameStatus", target = "gameStatus")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapLinkedTagsToResponses")
    @Mapping(target = "environmentCount", expression = "java(game.getEnv() != null ? game.getEnv().size() : 0)")
    @Mapping(target = "tagCount", expression = "java(game.getTags() != null ? game.getTags().size() : 0)")
    GameSummaryResponse toSummaryResponseWithTags(Game game);

    /**
     * Game 엔티티를 GameDetailResponse로 완전 변환 (태그, 환경 포함)
     */
    @Mapping(source = "gameStatus", target = "gameStatus")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapLinkedTagsToResponses")
    @Mapping(source = "env", target = "environments", qualifiedByName = "mapLinkedEnvsToResponses")
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    GameDetailResponse toDetailResponseComplete(Game game);

    /**
     * LinkedTag를 GameTagResponse로 변환
     */
    @Mapping(source = "gameTag.tagType", target = "tagType")
    @Mapping(source = "gameTag.tagType", target = "name", qualifiedByName = "tagTypeName")
    @Mapping(source = "gameTag.tagType", target = "description", qualifiedByName = "tagTypeDescription")
    GameTagResponse toTagResponse(LinkedTag linkedTag);

    /**
     * LinkedTag Set을 GameTagResponse Set으로 변환하는 헬퍼 메서드
     */
    @Named("mapLinkedTagsToResponses")
    default Set<GameTagResponse> mapLinkedTags(Set<LinkedTag> linkedTags) {
        if (linkedTags == null || linkedTags.isEmpty()) {
            return new HashSet<>();
        }
        return linkedTags.stream()
                .map(this::toTagResponse)
                .collect(Collectors.toSet());
    }

    /**
     * LinkedEnv Set을 EnvironmentResponse Set으로 변환하는 헬퍼 메서드
     */
    @Named("mapLinkedEnvsToResponses")
    default Set<EnvironmentResponse> mapLinkedEnvs(Set<LinkedEnv> linkedEnvs) {
        if (linkedEnvs == null || linkedEnvs.isEmpty()) {
            return new HashSet<>();
        }
        return linkedEnvs.stream()
                .map(this::toEnvironmentResponse)
                .collect(Collectors.toSet());
    }

    /**
     * EnvType enum을 문자열로 변환하는 헬퍼 메서드
     */
    @Named("envTypeName")
    default String mapEnvTypeName(EnvType envType) {
        return envType != null ? envType.name() : null;
    }

    /**
     * GameTagType에서 한글 이름을 가져오는 헬퍼 메서드
     */
    @Named("tagTypeName")
    default String getTagTypeName(GameTagType tagType) {
        return tagType != null ? tagType.getKoreanName() : null;
    }

    /**
     * GameTagType에서 설명을 가져오는 헬퍼 메서드
     */
    @Named("tagTypeDescription")
    default String getTagTypeDescription(GameTagType tagType) {
        return tagType != null ? tagType.getDescription() : null;
    }
}