//package com.imfine.ngs.game.controller;
//
//import com.imfine.ngs.game.service.MainPageService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//
///**
// * NGS(New Game Studio) 메인 페이지 호출 API 컨트롤러 클래스.
// * 메인 페이지와 관려된 게임{@link com.imfine.ngs.game.entity.Game} 데이터를 제공합니다.
// *
// * @author chan
// */
//@Tag(name = "Search", description = "메인페이지 게임 검색 API")
//@RequiredArgsConstructor
//@RequestMapping("/api/main")
//@RestController
//public class NGSController {
//
//    private final MainPageService mainPageService;
//
//    /**
//     * NGS 메인 페이지 초기 로드 (각 섹션 0 페이지)
//     * // 추천 엔티티 추가
//     */
//    @Operation(summary = "게임 추천 게임 조회", description = "게임 검색 API")
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping
//    public PagedSectionResponse NGSMainPage(@PageableDefault(sort = "name") Pageable pageable) {
//        // 각 섹션의 첫 번째 페이지(page=0, size=5) 반환
//        return mainPageService.getRecommendGames(pageable);
//    }
//
//    // 인기 게임 더보기
//    @Operation(summary = "인기 게임 더 보기", description = "이동시 인기 게임 목록을 페이지네이션으로 조회합니다.")
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/popular")
//    public PagedSectionResponse popularGamesPage(@PageableDefault(size = 5, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        return mainPageService.getPopularGames(pageable);
//    }
//
//
//    // 신작 게임 더보기
//    @Operation(summary = "신작 게임 더 보기", description = "이동시 신작 게임 목록을 페이지네이션으로 조회합니다.")
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/newGames")
//    public PagedSectionResponse newGamesPage(@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        return mainPageService.getNewGames(pageable);
//    }
//
//    // 추천 게임 더보기
//    @Operation(summary = "추천 게임 더 보기", description = "이동시 추천 게임 목록을 페이지네이션으로 조회합니다.")
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/recommended")
//    public PagedSectionResponse recommendedGamesPage(@PageableDefault(size = 5) Pageable pageable) {
//
//        return mainPageService.getRecommendGames(pageable);
//    }
//
//    // 할인 게임 더보기
//    @Operation(summary = "할인 게임 더 보기", description = "이동시 할인 중인 게임 목록을 페이지네이션으로 조회합니다.")
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/discount")
//    public PagedSectionResponse discountGamesPage(@PageableDefault(size = 5, sort = "discountRate", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        return mainPageService.getDiscountGames(pageable);
//    }
//}
