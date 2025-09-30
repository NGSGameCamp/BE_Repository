package com.imfine.ngs.game.dto.mapper.helper;

import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameTagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game 엔티티 매핑 관련 공통 유틸리티 클래스
 * GameDetailMapper와 GameCardMapper에서 공통으로 사용하는 로직을 제공
 *
 * @author chan
 */
@Slf4j
@Component
public class GameMapperHelper {

    /**
     * LinkedTag Set을 태그 이름 List로 변환
     *
     * @param linkedTags LinkedTag Set
     * @return 태그 이름 List
     */
    public List<String> extractTagNames(Set<LinkedTag> linkedTags) {
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
     * 리뷰 개수 계산
     *
     * @param reviews 리뷰 리스트
     * @param excludeDeleted 삭제된 리뷰 제외 여부
     * @return 리뷰 개수
     */
    public Integer calculateReviewCount(List<Review> reviews, boolean excludeDeleted) {
        if (reviews == null) {
            return 0;
        }

        if (excludeDeleted) {
            return (int) reviews.stream()
                    .filter(review -> review != null && !review.isDeleted())
                    .count();
        } else {
            return reviews.size();
        }
    }

    /**
     * 리뷰 평균 점수 계산
     *
     * @param reviews 리뷰 리스트
     * @param excludeDeleted 삭제된 리뷰 제외 여부
     * @return 평균 점수 (소수점 첫째 자리)
     */
    public Double calculateAverageScore(List<Review> reviews, boolean excludeDeleted) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        var stream = reviews.stream()
                .filter(review -> review != null && review.getScore() != null);

        if (excludeDeleted) {
            stream = stream.filter(review -> !review.isDeleted());
        }

        double average = stream
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
    public Integer calculateCurrentDiscountRate(List<SingleGameDiscount> discounts) {
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