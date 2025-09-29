//package com.imfine.ngs.game.controller.search;
//
//import com.imfine.ngs.game.dto.request.GameSearchRequest;
//import com.imfine.ngs.game.dto.response.GameDetailResponse;
//import com.imfine.ngs.game.dto.response.GameSummaryResponse;
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.enums.EnvType;
//import com.imfine.ngs.game.enums.GameTag;
//import com.imfine.ngs.game.enums.SortType;
//import com.imfine.ngs.game.service.search.GameSearchService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * {@link com.imfine.ngs.game.entity.Game} 조회 API 컨트롤러 클래스.
// * TODO: 조회 관련 예외 처리가 필요하다.
// *
// * @author chan
// */
//@Tag(name = "Search", description = "게임 검색 API")
//@RequiredArgsConstructor
//@RequestMapping("/api/games/search")
//@RestController
//public class GameSearchController {
//
//    // 서비스 클래스 추가
//    private final GameSearchService gameSearchService;
//
//    // 모든 게임 조회 api
//    @Operation(summary = "게임 목록 조회", description = "활성화 된 모든 게임을 페이지네이션으로 조회합니다.")
//    @GetMapping
//    public ResponseEntity<Page<GameSummaryResponse>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false, defaultValue = "NAME_ASC") SortType sort) {
//
//        // 페이지 처리 객체 생성
//        Pageable pageable = PageRequest.of(page, size, SortType.toSort());
//
//        // 데이터 조회
//        Page<GameSummaryResponse> allGames = gameSearchService.findAll(pageable);
//
//        // status 200 반환
//        return ResponseEntity.ok(allGames);
//    }
//
//    /**
//     * 단일 게임 조회 API
//     *
//     * @param id 게임 ID
//     * @return 게임 정보 (활성 상태인 게임만 조회 가능)
//     */
//    @Operation(summary = "게임 단일 조회", description = "활성화 된 모든 게임을 단일 조회합니다.")
//    @GetMapping("/{id}")
//    public ResponseEntity<GameDetailResponse> findById(@PathVariable Long id) {
//
//        // 데이터 조회
//        GameDetailResponse game = gameSearchService.findActiveById(id);
//
//        return ResponseEntity.ok(game);
//    }
//
//    // 게임 이름으로 조회
//    @Operation(summary = "게임 이름 조회", description = "활성화 된 모든 게임을 단일 조회합니다.")
//    @GetMapping("/name")
//    public ResponseEntity<Page<GameSummaryResponse>> searchByName(@RequestParam String keyword, @PageableDefault(size = 20) Pageable pageable) {
//
//        // 데이터 조회
//        Page<GameSummaryResponse> games = gameSearchService.searchByName(keyword, pageable);
//
//        return ResponseEntity.ok(games);
//    }
//
//    // 게임 태그로 조회
//    @Operation(summary = "게임 태그 조회", description = "활성화 된 모든 게임을 태그로 조회합니다.")
//    @GetMapping("/tag")
//    public ResponseEntity<Page<GameSummaryResponse>> searchByTag(@RequestParam String tag, @PageableDefault(size = 20) Pageable pageable) {
//
//        // 데이터 조회
//        Page<GameSummaryResponse> games = gameSearchService.findByTag(tag, pageable);
//
//        return ResponseEntity.ok(games);
//    }
//
//    // TODO: 특정 가격 이하 조회 로직 추가 (ex. 0 ~ 20000 이하)
////    public ResponseEntity<GameSummaryResponse> underPrice() {
////
////        return null;
////    }
//
//    // TODO: 특정 가격 이상 조회 로직 추가 (ex. 10000 <= 이상)
////    public ResponseEntity<GameSummaryResponse> overPrice() {
////
////        return null;
////    }
//
//    // 게임 가격 범위로 조회
//    @Operation(summary = "게임 가격 조회", description = "활성화 된 모든 게임을 가격 범위로 조회합니다.")
//    @GetMapping("/price")
//    public ResponseEntity<Page<GameSummaryResponse>> searchByPrice(@RequestParam(required = false) Long minPrice, @RequestParam(required = false) Long maxPrice, @PageableDefault(size = 20) Pageable pageable) {
//
//        // 기본 값 설정
//        if (minPrice == null) {
//            minPrice = 0L;
//        }
//
//        if (maxPrice == null) {
//            maxPrice = Long.MAX_VALUE;
//        }
//
//        // 가격 조회
//        Page<GameSummaryResponse> games = gameSearchService.findByPriceBetween(minPrice, maxPrice, pageable);
//
//        return ResponseEntity.ok(games);
//    }
//
//    // 게임 환경으로 조회
//    @Operation(summary = "게임 목록 조회", description = "활성화 된 모든 게임을 페이지네이션으로 조회합니다.")
//    @GetMapping("/env")
//    public ResponseEntity<Page<GameSummaryResponse>> searchByEnv(@RequestParam EnvType env, @PageableDefault(size = 20) Pageable pageable) {
//
//        // 환경 조회
//        Page<GameSummaryResponse> games = gameSearchService.findByEnv(env, pageable);
//
//        return ResponseEntity.ok(games);
//    }
//
//    // 통합 겁색
//    @Operation(summary = "게임 목록 조회", description = "활성화 된 모든 게임을 페이지네이션으로 조회합니다.")
//    @PostMapping("/integrated")
//    public ResponseEntity<Page<GameSummaryResponse>> integratedSearch(@Valid @RequestBody GameSearchRequest request) {
//
//        // 통합 환경 조회
//        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), SortType.toSort());
//
//        // 게임 조회
//        Page<GameSummaryResponse> games = gameSearchService.integratedSearch(request, pageable);
//
//        return ResponseEntity.ok(games);
//    }
//}
