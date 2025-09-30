package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameTagType;
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
@Component
public class GameDetailMapper {

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
                .tagNames(extractTagNames(game.getTags()))
                .description(game.getDescription())
                .thumbnailUrl(game.getThumbnailUrl())
                .spec(game.getSpec())
                .publisher(null)
                .reviewCount(calculateReviewCount(game.getReviews()))
                .averageScore(calculateAverageScore(game.getReviews()))
                .mediaUrls(new HashSet<>())
                .build();
    }

    /**
     * LinkedTag Set을 태그 이름 Set으로 변환
     *
     * @param linkedTags LinkedTag Set
     * @return 태그 이름 Set
     */
    private Set<String> extractTagNames(Set<LinkedTag> linkedTags) {
        if (linkedTags == null || linkedTags.isEmpty()) {
            return new HashSet<>();
        }

        return linkedTags.stream()
                .filter(Objects::nonNull)
                .map(LinkedTag::getGameTag)
                .filter(Objects::nonNull)
                .map(GameTag::getTagType)
                .filter(Objects::nonNull)
                .map(GameTagType::getKoreanName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 리뷰 개수 계산
     *
     * @param reviews 리뷰 리스트
     * @return 리뷰 개수
     */
    private Integer calculateReviewCount(List<Review> reviews) {
        return reviews != null ? reviews.size() : 0;
    }

    /**
     * 리뷰 평균 점수 계산
     *
     * @param reviews 리뷰 리스트
     * @return 평균 점수 (소수점 첫째 자리)
     */
    private Double calculateAverageScore(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .filter(review -> review != null && review.getScore() != null)
                .mapToDouble(Review::getScore)
                .average()
                .orElse(0.0);

        // 소수점 첫째 자리로 반올림
        return Math.round(average * 10) / 10.0;
    }
}