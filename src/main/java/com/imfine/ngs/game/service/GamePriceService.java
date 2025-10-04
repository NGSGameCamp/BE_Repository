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
 * {@link com.imfine.ngs.game.controller.GamePriceController} 비즈니스 로직 클래스.
 *
 * @author chan
 */
@RequiredArgsConstructor
@Service
public class GamePriceService {

    private final GameRepository gameRepository;
    private final GameCardMapper gameCardMapper;

    /**
     * 모든 게임을 가격 오름차순으로 조회
     */
    public Page<GameCardResponse> findAllPriceGame(Pageable pageable) {
        Page<Game> games = gameRepository.findAllByPriceOrder(pageable);
        return games.map(gameCardMapper::toCardResponse);
    }

    /**
     * 가격 범위 내 게임 조회
     *
     * @param minPrice 최소 가격 (null 가능)
     * @param maxPrice 최대 가격 (null 가능)
     * @param pageable 페이징 정보
     * @return 가격 범위 내 게임 목록
     */
    public Page<GameCardResponse> findPriceRangeGames(Integer minPrice, Integer maxPrice, Pageable pageable) {

        // 기본값 설정
        int min = (minPrice != null) ? minPrice : 0;
        int max = (maxPrice != null) ? maxPrice : Integer.MAX_VALUE;

        // 가격 범위 조회
        Page<Game> games = gameRepository.findByPriceRange(min, max, GameStatusType.ACTIVE, pageable);
        return games.map(gameCardMapper::toCardResponse);
    }
}
