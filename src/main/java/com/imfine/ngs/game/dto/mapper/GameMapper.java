package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.request.GameUpdateRequest;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.dto.response.GameResponse;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.env.EnvironmentResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game Entity와 DTO 간의 변환을 담당하는 매퍼 클래스.
 * N+1 문제를 고려한 최적화된 변환 메서드를 제공합니다.
 *
 * @author chan
 */
@Slf4j
@Component
public class GameMapper {

    /**
     * GameCreateRequest를 Game 엔티티로 변환
     */
    public Game toEntity(GameCreateRequest request) {
        return Game.builder()
                .name(request.getName())
                .price(request.getPrice())
                .tag(request.getTag() != null ? request.getTag().getCode() : null)
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .isActive(true)
                .build();
    }

    /**
     * GameUpdateRequest로 기존 Game 엔티티 업데이트
     * TODO:
     */
    public void updateEntity(Game game, GameUpdateRequest request) {
        if (request.getName() != null) {
            game.setName(request.getName());
        }
        if (request.getPrice() != null) {
            game.setPrice(request.getPrice());
        }
        if (request.getTag() != null) {
            game.setTag(request.getTag().getCode());
        }
        if (request.getDescription() != null) {
            game.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            game.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getIsActive() != null) {
            game.setActive(request.getIsActive());
        }
        // environments 업데이트는 별도의 서비스 레이어에서 처리
    }

    /**
     * Game 엔티티를 GameResponse로 변환
     */
    public GameResponse toResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .tag(game.getTag())
                .description(game.getDescription())
                .thumbnailUrl(game.getThumbnailUrl())
                .isActive(game.isActive())
                .createdAt(game.getCreatedAt())
                .build();
    }

    /**
     * Game 엔티티를 GameDetailResponse로 변환 (환경 정보 포함)
     */
    public GameDetailResponse toDetailResponse(Game game) {
        return GameDetailResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .tag(game.getTag())
                .description(game.getDescription())
                .thumbnailUrl(game.getThumbnailUrl())
                .environments(game.getEnv() != null ?
                        game.getEnv().stream()
                            .map(this::toEnvironmentResponse)
                            .collect(Collectors.toSet()) :
                        Set.of())
                .isActive(game.isActive())
                .createdAt(game.getCreatedAt())
                .averageRating(null) // TODO: 평점 시스템 구현 후 연동
                .reviewCount(0L) // TODO: 리뷰 시스템 구현 후 연동
                .build();
    }

    /**
     * LinkedEnv를 EnvironmentResponse로 변환
     */
    private EnvironmentResponse toEnvironmentResponse(LinkedEnv linkedEnv) {
        return EnvironmentResponse.builder()
                .id(linkedEnv.getEnv().getId())
                .name(linkedEnv.getEnv().getEnvType().getDescription())
                .type(linkedEnv.getEnv().getEnvType().name())
                .build();
    }

    /**
     * Game 엔티티를 GameSummaryResponse로 변환
     * N+1 문제 방지: 환경 정보를 로드하지 않고 개수만 반환
     */
    public GameSummaryResponse toSummaryResponse(Game game) {
        // 환경 정보 개수 계산 (Lazy Loading 방지)
        int envCount = 0;
        if (Hibernate.isInitialized(game.getEnv())) {
            // 이미 로드된 경우에만 크기 계산
            envCount = game.getEnv().size();
        }

        return GameSummaryResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .tag(game.getTag())
                .thumbnailUrl(game.getThumbnailUrl())
                .isActive(game.isActive())
                .createdAt(game.getCreatedAt())
                .environmentCount(envCount)
                .build();
    }

    /**
     * Game 엔티티를 GameDetailResponse로 안전하게 변환
     * N+1 문제 고려: 환경 정보가 로드되지 않은 경우 처리
     */
    public GameDetailResponse toDetailResponseSafe(Game game) {
        try {
            // 환경 정보가 초기화되었는지 확인
            Set<EnvironmentResponse> environments = Set.of();
            if (Hibernate.isInitialized(game.getEnv()) && game.getEnv() != null) {
                environments = game.getEnv().stream()
                        .map(this::toEnvironmentResponse)
                        .collect(Collectors.toSet());
            } else {
                log.debug("Environment data not initialized for game ID: {}", game.getId());
            }

            return GameDetailResponse.builder()
                    .id(game.getId())
                    .name(game.getName())
                    .price(game.getPrice())
                    .tag(game.getTag())
                    .description(game.getDescription())
                    .thumbnailUrl(game.getThumbnailUrl())
                    .environments(environments)
                    .isActive(game.isActive())
                    .createdAt(game.getCreatedAt())
                    .averageRating(null)
                    .reviewCount(0L)
                    .build();
        } catch (Exception e) {
            log.error("Error converting Game to DetailResponse: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Game entity", e);
        }
    }
}