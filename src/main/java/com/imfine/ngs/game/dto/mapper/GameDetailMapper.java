package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameTagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
                .tags(extractTagNames(game.getTags()))
                .description(game.getDescription())
                .introduction(game.getIntroduction())
                .thumbnailUrl(game.getThumbnailUrl())
                .spec(game.getSpec())
                .reviewCount(calculateReviewCount(game.getReviews()))
                .averageScore(calculateAverageScore(game.getReviews()))
                .mediaUrls(game.getMediaUrls() != null ? game.getMediaUrls() : new ArrayList<>())
                .releaseDate(game.getCreatedAt() != null ? game.getCreatedAt().toLocalDate() : null)
                .discountRate(calculateCurrentDiscountRate(game.getDiscounts()))
                .publisherId(game.getPublisher() != null ? game.getPublisher().getId() : null)
                .publisherName(game.getPublisher() != null ? game.getPublisher().getName() : null)
                .env(extractEnvDescriptions(game.getEnv()))
                .build();
    }

    /**
     * LinkedTag Set을 태그 이름 List로 변환
     *
     * @param linkedTags LinkedTag Set
     * @return 태그 이름 List
     */
    private List<String> extractTagNames(Set<LinkedTag> linkedTags) {
        if (linkedTags == null || linkedTags.isEmpty()) {
            return new ArrayList<>();
        }

        return linkedTags.stream()
                .filter(Objects::nonNull)
                .map(LinkedTag::getGameTag)
                .filter(Objects::nonNull)
                .map(GameTag::getTagType)
                .filter(Objects::nonNull)
                .map(GameTagType::getKoreanName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    /**
     * 현재 유효한 할인율 계산
     * 여러 할인이 있을 경우 가장 높은 할인율을 반환
     *
     * @param discounts 할인 리스트
     * @return 현재 유효한 할인율 (%), 할인이 없으면 0
     */
    private Integer calculateCurrentDiscountRate(List<SingleGameDiscount> discounts) {
        if (discounts == null || discounts.isEmpty()) {
            return 0;
        }

        return findMaxActiveDiscountRate(discounts, LocalDateTime.now());
    }

    /**
     * 활성화된 할인 중 최대 할인율 찾기
     */
    private Integer findMaxActiveDiscountRate(List<SingleGameDiscount> discounts, LocalDateTime now) {
        return discounts.stream()
                .filter(Objects::nonNull)
                .filter(discount -> isDiscountActive(discount, now))
                .mapToInt(this::toIntegerRate)
                .max()
                .orElse(0);
    }

    /**
     * 할인이 현재 활성화 상태인지 확인
     */
    private boolean isDiscountActive(SingleGameDiscount discount, LocalDateTime now) {
        return isStarted(discount.getCreatedAt(), now) &&
                !isExpired(discount.getExpiresAt(), now);
    }

    /**
     * 할인이 시작되었는지 확인
     */
    private boolean isStarted(LocalDateTime startTime, LocalDateTime now) {
        return startTime == null || !startTime.isAfter(now);
    }

    /**
     * 할인이 만료되었는지 확인
     */
    private boolean isExpired(LocalDateTime expiryTime, LocalDateTime now) {
        return expiryTime != null && !expiryTime.isAfter(now);
    }

    /**
     * 할인율을 정수로 변환
     */
    private int toIntegerRate(SingleGameDiscount discount) {
        BigDecimal rate = discount.getDiscountRate();
        return rate == null ? 0 : rate.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}