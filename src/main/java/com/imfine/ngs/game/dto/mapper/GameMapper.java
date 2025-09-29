package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.request.GameUpdateRequest;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.EnvironmentResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.enums.EnvType;
import org.mapstruct.*;

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
    @Mapping(target = "env", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Game toEntity(GameCreateRequest request);

    /**
     * GameUpdateRequest로 기존 Game 엔티티 업데이트
     * null 값은 무시하고 업데이트
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "env", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", source = "isActive")
    void updateEntity(GameUpdateRequest dto, @MappingTarget Game entity);

    /**
     * Game 엔티티를 GameSummaryResponse로 변환
     */
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "environmentCount", ignore = true)
    GameSummaryResponse toSummaryResponse(Game game);

    /**
     * Game 엔티티를 GameDetailResponse로 변환
     */
    @Mapping(source = "active", target = "isActive")
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
     * EnvType enum을 문자열로 변환하는 헬퍼 메서드
     */
    @Named("envTypeName")
    default String mapEnvTypeName(EnvType envType) {
        return envType != null ? envType.name() : null;
    }
}