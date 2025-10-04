package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * {@link com.imfine.ngs.game.controller.GameReleaseController} 비즈니스 로직 클래스.
 *
 * @author chan
 */
@RequiredArgsConstructor
@Service
public class GameReleasedService {

    // gameRepository
    private final GameRepository gameRepository;

    private final GameCardMapper gameCardMapper;

    // 모든 기간 조회
    public Page<GameCardResponse> findAllReleased(Pageable pageable) {

        // 모든 게임 객체 조회 (최신 순)
        Page<Game> gameList = gameRepository.findAllRelease(pageable);

        return gameList.map(gameCardMapper::toCardResponse);
    }

    // 최근 N개월 이내 게임 조회
    // 1개월, 3개월, 1년이 조건만 같고 쿼리는 동일하므로 하나의 메서드로 처리
    public Page<GameCardResponse> findByMonth(int months, Pageable pageable) {

        // N개월 전 날짜 계산
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);

        // 해당 날짜 이후 출시된 게임 조회
        Page<Game> gameList = gameRepository.findReleasedAfter(startDate, pageable);

        // DTO로 변환하여 반환
        return gameList.map(gameCardMapper::toCardResponse);
    }
}
