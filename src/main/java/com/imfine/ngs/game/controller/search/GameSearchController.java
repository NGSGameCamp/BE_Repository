package com.imfine.ngs.game.controller.search;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.service.search.GameSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * {@link com.imfine.ngs.game.entity.Game} 조회 API 컨트롤러 클래스.
 *
 * @author chan
 */
@RequiredArgsConstructor
@RequestMapping("/api/games")
@RestController
public class GameSearchController {

    // 서비스 클래스 추가
    private final GameSearchService gameSearchService;

    // 모든 게임 조회 api
    @GetMapping
    public ResponseEntity<Page<Game>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false, defaultValue = "NAME_ASC") String sort) {

        // 정렬 기준 설정
        SortType sortType = SortType.valueOf(sort);

        // 전체 조회 서비스 메서드 호출
        Page<Game> games = gameSearchService.findAll(page, size, sortType);

        // status 200 반환
        return ResponseEntity.ok(games);
    }

    /**
     * 단일 게임 조회 API
     *
     * @param id 게임 ID
     * @return 게임 정보 (활성 상태인 게임만 조회 가능)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> findById(@PathVariable Long id) {
        try {
            Game game = gameSearchService.findActiveById(id);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 게임 이름으로 조회

    // 게임 태그로 조회

    // 게임 가격으로 조회

    // 게임 환경으로 조회
}
