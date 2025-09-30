package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * {@link com.imfine.ngs.game.entity.Game} 비즈니스 클래스.
 *
 * @author chan
 */
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

        // 변환하여 반환한다.
        return gameDetailMapper.toDetailResponse(detailGame);
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
        double averageScoreValues = minAverageScore != null ? minAverageScore : 3.5; // 평균 평ㅈ덤

        // DB에서 인기순으로 게임 조회
        Page<Game> popularGames = gameRepository.findPopularGames(
                GameStatusType.ACTIVE,
                reviewValues,
                averageScoreValues,
                pageable
        );

        // 각 게임을 GameCardResponse로 변환하여 반환.
        return popularGames.map(gameCardMapper::toCardResponse);
    }

}
