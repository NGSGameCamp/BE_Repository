package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link com.imfine.ngs.game.entity.Game} 비즈니스 클래스.
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameDetailMapper gameDetailMapper;

    /**
     * 게임 상세 정보를 조회합니다.
     * TODO: review / discounts 로직 분리
     * @param id 조회할 게임의 ID
     * @return GameDetailResponse 게임 상세 정보
     */
    public GameDetailResponse getGameDetail(Long id) {
        // DB에서 상세 정보를 조회한다.
        Game detailGame = gameRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Game not found " + id));

        // review 별도 조회
        List<Review> reviews = gameRepository.findActiveReviewsByGameId(id);

        // discounts 별도 조회
        List<SingleGameDiscount> discounts = gameRepository.findActiveDiscountsByGameId(id);

        // 변환하여 반환한다.
        return gameDetailMapper.toDetailResponse(detailGame, reviews, discounts);
    }
}
