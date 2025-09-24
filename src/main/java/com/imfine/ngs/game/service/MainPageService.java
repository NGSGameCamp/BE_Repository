package com.imfine.ngs.game.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.service.search.GameSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link com.imfine.ngs.game.controller.NGSController} 메인 페이지 서비스 클래스.
 * 차후 추천, 인기, 할인의 리팩토링이 필요하다.
 * TODO: MainPageService가  {@link GameSearchService}에 의존하고 있다. 이를 인터페이스를 활용하여 분리할 수 없을까?
 * TODO: 서비스의 코드 중복을 제거할 수 있다.
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MainPageService {

    private final GameSearchService searchService;

    // NGS MainPage 조회
    // 1. 신작 게임 조회 (추가된 최신 날짜 게임 5개)
    // 2. 추천 게임 조회 (무작위 게임 5개 추가)
    // 3. 인기 TOP 게임 조회 (무작위 게임 5개 추가)
    // 4. 할인 중인 게임 조회 (무작위 게임 5개 추가)

    /**
     * NGS MainPage 조회
     * 신작, 추천, 인기 TOP, 할인 중인 게임 데이터를 한번에 조회
     * TODO: 예외는 GlobalExceptionHandler가 처리한다.
     *
     * @param newLimit       신작 게임 개수
     * @param recommendLimit 추천 게임 개수
     * @param popularLimit   인기 게임 개수
     * @param discountLimit  할인 게임 개수
     * @return 메인페이지 게임 데이터
     */
    public Map<String, Object> findAllMainPageGameData(
            int newLimit,
            int recommendLimit,
            int popularLimit,
            int discountLimit
    ) {

        Map<String, Object> mainPageGameData = new HashMap<>();

        // 1. 신작 게임 조회
        List<Game> newGames = getNewGames(newLimit);
        mainPageGameData.put("newGames", newGames);

        // 2. 추천 게임 조회
        List<Game> recommendedGames = getRecommendGames(recommendLimit);
        mainPageGameData.put("recommendedGames", recommendedGames);

        // 3. 인기 게임 조회
        List<Game> popularGames = getPopularGames(popularLimit);
        mainPageGameData.put("popularGames", popularGames);

        // 4. 할인 게임 조회
        List<Game> discountGames = getDiscountGames(discountLimit);
        mainPageGameData.put("discountGames", discountGames);

        log.debug("메인 페이지 데이터 조회 성공!");

        // 메인 페이지 게임 데이터 반환
        return mainPageGameData;
    }

    // 기본값으로 메인페이지 데이터 5개 조회
    public Map<String, Object> findAllMainPageGameData() {
        return findAllMainPageGameData(5, 5, 5, 5);
    }

    /**
     * 신작 게임 조회 (90일간 추가된 게임을 조회한다.)
     *
     * @param limit 조회할 신작 게임 개수
     * @return 신작 게임 리스트
     */
    // 신작 게임 조회
    public List<Game> getNewGames(int limit) {

        log.debug("신작 게임 {} 개 조회", limit);

        // 최신 날짜 기준으로 게임 조회
        Page<Game> gamePage = searchService.findByCreatedAt(0, limit, SortType.DATE_DESC);
        List<Game> allGames = gamePage.getContent();

        // 최근 90일 이내 게임 필터링 (선택)
        LocalDateTime threeMonthAgo = LocalDateTime.now().minusMonths(3);

        List<Game> newGames = allGames.stream()
                .filter(game -> game.getCreatedAt() != null && game.getCreatedAt().isAfter(threeMonthAgo))
                .limit(limit)
                .toList();

        log.debug("조회하여 가져온 3달간 추가된 신작 게임 리스트: {}", newGames);

        return newGames;
    }

    // 추천 게임 조회

    /**
     * 추천 게임 조회 메서드 (사용자 알고리즘 기반)
     * TODO: 현재는 무작위 게임 5개를 반환한다.
     *
     * @param limit 조회할 추천 게임 개수
     * @return 추천 게임 리스트
     */
    public List<Game> getRecommendGames(int limit) {

        // 로그
        log.debug("추천 게임 {}개 조회", limit);

        // 전체 게임 조회
        Page<Game> gamePage = searchService.findAll(0, limit, SortType.DATE_DESC);
        List<Game> allGames = gamePage.getContent();

        // 리스트가 limit보다 작으면 전체 반환
        if (allGames.size() < limit) {
            return allGames;
        }

        // 추천 게임 조회 -> 현재는 무작위로 5개를 가져온다.
        List<Game> shuffle = new ArrayList<>(allGames);
        Collections.shuffle(shuffle);

        var recommendGameList = shuffle.stream()
                .limit(limit)
                .toList();

        log.debug("조회한 추천 게임(현재는 무작위 5개): {}", recommendGameList);

        return recommendGameList;
    }

    // 인기 TOP 게임 조회

    /**
     * 인기 TOP 게임 조회 - 현재는 무작위, 추후 REVIEW 테이블의 socre 를 통해 조회
     * TODO: REVIEW 엔티티 생성 후 score 필드를 통해 검색
     *
     * @param limit 조회할 추천 게임 수
     * @return 인기 게임 리스트
     */
    public List<Game> getPopularGames(int limit) {

        // (로그) 가져온 게임 개수
        log.debug("조회한 인기 게임 개수: {}", limit);

        // 전체 게임 조회
        Page<Game> gamePage = searchService.findAll(0, limit, SortType.DATE_DESC);
        List<Game> allGames = gamePage.getContent();

        // 리스트가 limit보다 작으면 전체 반환
        if (allGames.size() < limit) {
            return allGames;
        }

        // 무작위 게임 반환
        List<Game> gameList = new ArrayList<>(allGames);
        Collections.shuffle(gameList);

        List<Game> popularGames = gameList.stream()
                .limit(limit)
                .toList();

        // (로그) 반환한 게임 리스트 객체
        log.debug("조회한 인기순 게임(현재는 무작위 5개): {}", popularGames);

        return popularGames;
    }

    /**
     * 할인 중인 게임 조회 - 현재는 무작위 게임 반환, 차후 SINGLE_GAME_DISCOUNT 테이블 추가 후 created_at을 통해 검사
     * TODO: SINGLE_GAME_DISCOUNT 테이블 추가
     *
     * @param limit 조회한 할인 중인 게임 수
     * @return 할인 중 게임 리스트
     */
    public List<Game> getDiscountGames(int limit) {

        // (로그) 가져온 게임 개수
        log.debug("조회한 할인 중인 게임 갯수 {}", limit);

        // 전체 게임 조회
        Page<Game> gamePage = searchService.findAll(0, limit, SortType.DATE_DESC);
        List<Game> allGames = gamePage.getContent();

        // 리스트가 limit보다 작으면 전체 반환
        if (allGames.size() < limit) {
            return allGames;
        }

        // 무작위 정렬
        List<Game> gameList = new ArrayList<>(allGames);
        Collections.shuffle(gameList);

        // 정렬한 객체 생성
        List<Game> discountGames = gameList.stream()
                .limit(limit)
                .toList();

        // (로그) 가져온 할인 중 게임
        log.debug("조회한 할인 중인 게임: {}", discountGames);

        return discountGames;
    }

}
