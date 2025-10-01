package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final GameCardMapper gameCardMapper;

    /**
     * 게임 상세 정보를 조회합니다.
     *
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
        var returnGame = gameDetailMapper.toDetailResponse(detailGame, reviews, discounts);

        System.out.println(returnGame.toString());

        return returnGame;
    }

    /**
     * 게임 추천 정보(게임 인기순) 조회.
     *
     * @param pageable 페이징 처리
     * @return 게임 간단 정보
     */
    public Page<GameCardResponse> getPopular(Pageable pageable, Integer minReviews, Double minAverageScore) {

        // 파라미터 기본값 설정
        long reviewValues = minReviews != null ? minReviews.longValue() : 3L; // 리뷰 개수
        double averageScoreValues = minAverageScore != null ? minAverageScore : 3.5; // 평균 평점

        // 1단계: 조건에 맞는 게임 ID들 조회
        Page<Long> gameIdPage = gameRepository.findPopularGameIds(
                GameStatusType.ACTIVE,
                reviewValues,
                averageScoreValues,
                pageable
        );

        // 2단계: ID가 없으면 빈 페이지 반환
        if (!gameIdPage.hasContent()) {
            return Page.empty(pageable);
        }

        // 3단계: ID로 게임 상세 정보 조회
        List<Game> games = gameRepository.findGamesWithDetailsByIds(gameIdPage.getContent());

        // 4단계: Page 객체 재구성
        Page<Game> popularGames = new PageImpl<>(
                games,
                pageable,
                gameIdPage.getTotalElements()
        );

        // 5단계: GameCardResponse로 변환하여 반환
        return popularGames.map(gameCardMapper::toCardResponse);
    }

}
