package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * NGS(New Game Studio) 메인 페이지 호출 API 컨트롤러 클래스.
 * {@link com.imfine.ngs.game.entity.Game}을 메인화면 레이어별로 조회한다.
 *
 * @author chan
 */
@RequiredArgsConstructor
@RequestMapping("/api/main")
@RestController
public class NGSController {

    private final MainPageService mainPageService;

    /**
     * NGS 메인 페이지 조회 API
     *
     * @param popularLimit     인기 게임 개수 (기본 값: 5)
     * @param newRegisterLimit 신작 게임 개수 (기본 값: 5)
     * @param recommendedLimit 추천 게임 개수 (기본 값: 5)
     * @return 메인페이지 섹별별 게임 데이터
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> NGSMainPage(
            @RequestParam(defaultValue = "5") int popularLimit,
            @RequestParam(defaultValue = "5") int newRegisterLimit,
            @RequestParam(defaultValue = "5") int recommendedLimit) {

        // 메인 페이지에 표시할 서비스 로직 호출
        // TODO: Map<String, Object>를 Map<String, List<Game>> 으로 바꾸는 것이 어떠할까?
        Map<String, Object> mainPageGameData = mainPageService.findAllMainPageGameData();

        return ResponseEntity.ok(mainPageGameData);
    }
}
