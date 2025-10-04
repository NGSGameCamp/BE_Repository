package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
     * TODO: review / discounts 로직 분리
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
        return gameDetailMapper.toDetailResponse(detailGame, reviews, discounts);
    }

    // 추천 게임 조회
    public Page<GameCardResponse> getRecommendGame(Pageable pageable) {

        // 엔티티 조회
        Page<Game> recommendGames = gameRepository.findRecommendedGame(GameStatusType.ACTIVE, pageable);

        return recommendGames.map(gameCardMapper::toCardResponse);
    }

    // 게임 태그로 검색
    public Page<GameCardResponse> getGameTags(@RequestParam List<String> tagCode, Pageable pageable) {

        // 태그 코드를 GameTagType으로 변환
        List<GameTagType> gameTagTypes = tagCode.stream()
                .map(GameTagType::fromCode)
                .toList();

        // 게임 조회 (모든 태그를 포함하는 게임만)
        Page<Game> gameList = gameRepository.findByTagsAndStatus(
                gameTagTypes,
                gameTagTypes.size(),
                GameStatusType.ACTIVE,
                pageable
        );

        // dto로 변환
        return gameList.map(gameCardMapper::toCardResponse);
    }

    /**
     * 게임 이름으로 검색합니다.
     *
     * @param gameTitle 검색할 게임 이름
     * @param pageable 페이징 정보
     * @return Page<GameCardResponse> 검색된 게임 목록
     */
    public Page<GameCardResponse> getSearchByTitle(String gameTitle, Pageable pageable) {

        // 검색 입력 검증 - 빈 값이면 빈 페이지 반환
        if (gameTitle == null || gameTitle.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        // repository에서 게임 조회 (ACTIVE 상태만)
        Page<Game> gameList = gameRepository.findByGameTitle(
                gameTitle.trim(),
                GameStatusType.ACTIVE,
                pageable
        );

        return gameList.map(gameCardMapper::toCardResponse);
    }
}
