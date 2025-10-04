package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * {@link com.imfine.ngs.game.entity.Game} 평균 평점 조회 비즈니스 클래스.
 *
 * @author chan
 */
@RequiredArgsConstructor
@Service
public class GameAverageScoreService {

    private final GameRepository gameRepository;
    private final GameCardMapper gameCardMapper;

    /**
     * 평균 평점 범위로 게임 조회
     *
     * @param minAverage 최소 평균 평점 (null 가능, 기본값 0.0)
     * @param maxAverage 최대 평균 평점 (null 가능, 기본값 5.0)
     * @param pageable 페이징 정보
     * @return 평균 평점 범위 내 게임 목록
     */
    public Page<GameCardResponse> findAverageScore(Double minAverage, Double maxAverage, Pageable pageable) {

        // 기본값 설정
        double min = (minAverage != null) ? minAverage : 0.0;
        double max = (maxAverage != null) ? maxAverage : 5.0;

        // 예외 처리: minAverage가 maxAverage보다 큰 경우
        if (min > max) {
            throw new IllegalArgumentException("최소 평점은 최대 평점보다 클 수 없습니다.");
        }

        // 평점 범위 유효성 검증 (0.0 ~ 5.0)
        if (min < 0.0 || max > 5.0) {
            throw new IllegalArgumentException("평점은 0.0에서 5.0 사이의 값이어야 합니다.");
        }

        // DB 조회
        Page<Game> gameList = gameRepository.findByAverageScore(min, max, GameStatusType.ACTIVE, pageable);

        // DTO 변환 및 반환
        return gameList.map(gameCardMapper::toCardResponse);
    }
}
