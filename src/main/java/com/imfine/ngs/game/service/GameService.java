package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.mapper.GameDetailMapper;
import com.imfine.ngs.game.dto.request.GameSearchRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

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
    private final Executor gameQueryExecutor;

    /**
     * 게임 상세 정보를 조회합니다.
     * TODO: review / discounts 로직 분리
     *
     * @param id 조회할 게임의 ID
     * @return GameDetailResponse 게임 상세 정보
     */
    public GameDetailResponse getGameDetail(Long id) {
        log.debug("Starting getGameDetail on thread: {}", Thread.currentThread());

        // 게임 기본 정보 조회
        Game detailGame = gameRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Game not found" + id));

        // review 별도 조회 -> 스레드 처리
        CompletableFuture<List<Review>> reviews =
                CompletableFuture.supplyAsync(() -> {
                    log.debug("Getting reviews on Thread: {}", Thread.currentThread());
                    return gameRepository.findActiveReviewsByGameId(id);
                }, gameQueryExecutor);

        // discounts 별도 조회 -> 스레드 처리
        CompletableFuture<List<SingleGameDiscount>> discounts =
                CompletableFuture.supplyAsync(() -> {
                    log.debug("Getting discounts on Thread: {}", Thread.currentThread());
                    return gameRepository.findActiveDiscountsByGameId(id);
                }, gameQueryExecutor);

        // 두 작업이 모두 완료될 때까지 대기
        CompletableFuture.allOf(reviews, discounts).join();

        // 결과 가져오기 (이미 완료됨, 즉시 반환)
        List<Review> reviewList = reviews.join();
        List<SingleGameDiscount> singleGameDiscountList = discounts.join();

        log.debug("Ending getGameDetail on thread: {}", Thread.currentThread());
        // 변환하여 반환한다.
        return gameDetailMapper.toDetailResponse(detailGame, reviewList, singleGameDiscountList);
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
     * 다양한 조건으로 게임 검색 (QueryDSL 사용)
     *
     * @param request  검색 조건 (이름, 태그, 가격, 정렬 등)
     * @param pageable 페이징 정보
     * @return 검색된 게임 목록
     */
    public Page<GameCardResponse> searchGames(GameSearchRequest request, Pageable pageable) {
        log.debug("🔍 Search Request - name: {}, tag: {}, tags: {}, minPrice: {}, maxPrice: {}",
                request.getName(), request.getTag(), request.getTags(),
                request.getMinPrice(), request.getMaxPrice());

        // 복수 태그 검색이면 우선순위 정렬 적용
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            log.debug("✅ Using Priority Search");
            return searchGamesWithPriority(request, pageable);
        }

        log.debug("📋 Using Standard QueryDSL Search");
        // QueryDSL을 통한 동적 쿼리 실행
        Page<Game> games = gameRepository.searchGames(request, pageable);

        // dto로 변환
        return games.map(gameCardMapper::toCardResponse);
    }

    /**
     * 우선순위 정렬: 태그 일치 개수가 많을수록 상위 노출
     */
    private Page<GameCardResponse> searchGamesWithPriority(
            GameSearchRequest request, Pageable pageable) {

        List<GameTagType> searchTags = request.getTags();
        log.debug("🔍 Priority Search - Tags: {}", searchTags);

        // 1. 기본 필터 조건으로 후보 게임 조회 (태그는 OR 조건)
        List<Game> candidateGames = gameRepository.findCandidateGamesForPrioritySearch(
                request.getName(),
                request.getMinPrice(),
                request.getMaxPrice(),
                searchTags,
                GameStatusType.ACTIVE
        );

        log.debug("📦 Total Candidates: {}", candidateGames.size());

        // 2. 각 게임의 태그 일치도 계산 및 정렬
        List<GameWithScore> scoredGames = candidateGames.stream()
                .map(game -> {
                    // 게임의 태그와 검색 태그의 교집합 개수 계산
                    long matchCount = game.getTags().stream()
                            .map(linkedTag -> linkedTag.getGameTag().getTagType())
                            .filter(searchTags::contains)
                            .count();

                    log.debug("Game: {} | Tags: {} | Match Score: {} | Created: {}",
                            game.getName(),
                            game.getTags().stream()
                                    .map(lt -> lt.getGameTag().getTagType())
                                    .collect(Collectors.toList()),
                            matchCount,
                            game.getCreatedAt());

                    return new GameWithScore(game, matchCount);
                })
                .filter(gs -> {
                    // 최소 1개 이상 일치하는 게임만 포함
                    boolean hasMatch = gs.getMatchScore() > 0;
                    if (!hasMatch) {
                        log.debug("❌ Filtered out: {} (score: 0)", gs.getGame().getName());
                    }
                    return hasMatch;
                })
                .sorted(Comparator
                        .comparing(GameWithScore::getMatchScore).reversed() // 1순위: 태그 일치도 (내림차순)
                        .thenComparing(gs -> gs.getGame().getCreatedAt(), Comparator.reverseOrder()) // 2순위: 최신순
                        .thenComparing(gs -> gs.getGame().getName()) // 3순위: 이름 (오름차순)
                )
                .collect(Collectors.toList());

        log.debug("✅ After Priority Filtering: {} games", scoredGames.size());

        // 상위 10개 로그 출력
        for (int i = 0; i < Math.min(10, scoredGames.size()); i++) {
            GameWithScore gs = scoredGames.get(i);
            log.debug("  {}. {} - Score: {} | Created: {}",
                    i + 1,
                    gs.getGame().getName(),
                    gs.getMatchScore(),
                    gs.getGame().getCreatedAt());
        }

        // 3. 페이징 처리
        int totalElements = scoredGames.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), totalElements);

        if (start >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        }

        List<GameCardResponse> pageContent = scoredGames.subList(start, end).stream()
                .map(gs -> gameCardMapper.toCardResponse(gs.getGame()))
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    /**
     * 게임 일치도 계산을 위한 내부 클래스
     */
    @Getter
    @AllArgsConstructor
    private static class GameWithScore {
        private Game game;
        private long matchScore; // 태그 일치 개수
    }

    /**
     * 게임 이름으로 검색합니다.
     *
     * @param gameTitle 검색할 게임 이름
     * @param pageable  페이징 정보
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
