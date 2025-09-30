package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.mapper.helper.GameMapperHelper;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Game 엔티티를 GameDetailResponse DTO로 변환하는 매퍼 클래스
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GameDetailMapper {

    private final GameMapperHelper helper;

    /**
     * Game 엔티티를 GameDetailResponse DTO로 변환
     *
     * @param game 변환할 Game 엔티티
     * @return GameDetailResponse DTO
     */
    public GameDetailResponse toDetailResponse(Game game) {

        if (game == null) {
            return null;
        }

        return GameDetailResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .tags(helper.extractTagNames(game.getTags()))
                .description(game.getDescription())
                .introduction(game.getIntroduction())
                .thumbnailUrl(game.getThumbnailUrl())
                .spec(game.getSpec())
                .reviewCount(helper.calculateReviewCount(game.getReviews(), false))
                .averageScore(helper.calculateAverageScore(game.getReviews(), false))
                .mediaUrls(game.getMediaUrls() != null ? game.getMediaUrls() : new ArrayList<>())
                .releaseDate(game.getCreatedAt() != null ? game.getCreatedAt().toLocalDate() : null)
                .discountRate(helper.calculateCurrentDiscountRate(game.getDiscounts()))
                .publisherId(game.getPublisher() != null ? game.getPublisher().getId() : null)
                .publisherName(game.getPublisher() != null ? game.getPublisher().getName() : null)
                .env(extractEnvDescriptions(game.getEnv()))
                .build();
    }

    /**
     * LinkedEnv Set을 환경(OS) 이름 List로 변환
     *
     * @param linkedEnvs LinkedEnv Set
     * @return 환경 이름 List
     */
    private List<String> extractEnvDescriptions(Set<LinkedEnv> linkedEnvs) {

        if (linkedEnvs == null || linkedEnvs.isEmpty()) {
            return new ArrayList<>();
        }

        return linkedEnvs.stream()
                .map(LinkedEnv::getEnvDescription)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}