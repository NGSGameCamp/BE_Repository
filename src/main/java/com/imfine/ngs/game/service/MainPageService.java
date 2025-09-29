package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameMapper;
import com.imfine.ngs.game.dto.response.GameSummaryResponse;
import com.imfine.ngs.game.dto.response.util.PagedSectionResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link com.imfine.ngs.game.controller.NGSController} 메인 페이지 서비스 클래스.
 * 차후 추천, 인기, 할인의 리팩토링이 필요하다.
 * 페이지네이션과 DTO를 활용한 메인 페이지 서비스
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MainPageService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    /**
     * 인기 게임 페이지 조회
     *
     * @param pageable 페이지 정보
     * @return 인기 게임 페이지 응답
     */
    public PagedSectionResponse getPopularGames(Pageable pageable) {
        log.debug("인기 게임 페이지 조회 - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        // TODO: 실제 인기도 기반 조회 구현 (현재는 임시)
        Page<Game> games = gameRepository.findByIsActiveTrue(pageable);
        return buildPagedResponse(games);
    }

    /**
     * 신작 게임 페이지 조회 (최근 90일)
     *
     * @param pageable 페이지 정보
     * @return 신작 게임 페이지 응답
     */
    public PagedSectionResponse getNewGames(Pageable pageable) {
        log.debug("신작 게임 페이지 조회 - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusDays(90);
        Page<Game> games = gameRepository.findByIsActiveTrueAndCreatedAtAfter(
            threeMonthsAgo, pageable);
        return buildPagedResponse(games);
    }

    /**
     * 추천 게임 페이지 조회
     * TODO: 실제 추천 알고리즘 구현
     *
     * @param pageable 페이지 정보
     * @return 추천 게임 페이지 응답
     */
    public PagedSectionResponse getRecommendGames(Pageable pageable) {
        log.debug("추천 게임 페이지 조회 - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        // TODO: 실제 추천 알고리즘 구현 (현재는 임시)
        Page<Game> games = gameRepository.findByIsActiveTrue(pageable);
        return buildPagedResponse(games);
    }

    /**
     * 할인 게임 페이지 조회
     * TODO: 실제 할인 정보 기반 조회 구현
     *
     * @param pageable 페이지 정보
     * @return 할인 게임 페이지 응답
     */
    public PagedSectionResponse getDiscountGames(Pageable pageable) {
        log.debug("할인 게임 페이지 조회 - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        // TODO: 실제 할인 정보 기반 조회 구현
        Page<Game> games = gameRepository.findByIsActiveTrue(pageable);
        return buildPagedResponse(games);
    }

//    public Page<Game> getGames(Pageable pageable) {
//        Page<Game> games = gameRepository.findAll(pageable);
//        return games;
//    }

    /**
     * Page<Game>을 PagedSectionResponse로 변환하는 helper 메서드
     * 프론트엔드에서 어떤 섹션인지 이미 알고 있으므로 sectionType은 불필요
     *
     * @param games Game 엔티티 페이지 정보
     * @return PagedSectionResponse DTO
     */
    private PagedSectionResponse buildPagedResponse(Page<Game> games) {
        List<GameSummaryResponse> gameSummaries = games.getContent().stream()
                .map(gameMapper::toSummaryResponse)
                .toList();

        return PagedSectionResponse.builder()
                .games(gameSummaries)
                .currentPage(games.getNumber())
                .totalPages(games.getTotalPages())
                .totalElements(games.getTotalElements())
                .hasNext(games.hasNext())
                .hasPrevious(games.hasPrevious())
                .build();
    }
}
