package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.mapper.helper.GameMapperHelper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Game 엔티티를 GameCardResponse DTO로 변환하는 매퍼 클래스
 * 목록 조회용 응답 DTO 생성을 담당
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GameCardMapper {

    private final GameMapperHelper helper;

    /**
     * Game 엔티티를 GameCardResponse DTO로 변환
     *
     * @param game 변환할 Game 엔티티
     * @return GameCardResponse DTO
     */
    public GameCardResponse toCardResponse(Game game) {
        if (game == null) {
            return null;
        }

        return GameCardResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .discountRate(helper.calculateCurrentDiscountRate(game.getDiscounts()))
                .tags(helper.extractTagNames(game.getTags()))
                .publisherId(game.getPublisher() != null ? game.getPublisher().getId() : null)
                .publisherName(game.getPublisher() != null ? game.getPublisher().getName() : null)
                .reviewCount(helper.calculateReviewCount(game.getReviews(), true))
                .averageScore(helper.calculateAverageScore(game.getReviews(), true))
                .releaseDate(game.getCreatedAt() != null ? game.getCreatedAt().toLocalDate() : null)
                .thumbnailUrl(game.getThumbnailUrl())
                .build();
    }
}
