package com.imfine.ngs.game.service.search;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.repository.GameRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임({@link Game}) 검색 서비스 클래스.
 * {@link Game}의 isActive가 활성화 되어야 조회가 가능하다.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
 * TODO: 각 조회 메서드에 정렬(Sort) 메서드가 중복되어 있다. 하나의 메서드로 통합해서 관리 할 수 있다.
 *
 * @author chan
 */
@RequiredArgsConstructor
@Service
public class GameSearchService {

    private final GameRepository gameRepository;

    // 게임 등록 로직 (테스트용)
    public void registerGame(Game game) {
        gameRepository.save(game);
    }

    // 게임 단일 조회 로직 (활성 상태인 게임만 조회)
    public Game findActiveById(Long id) {
        return gameRepository.findByIdAndIsActive(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));
    }

    // 게임 전체 조회 로직
    public Page<Game> findAll(int Page, int size, SortType sortType) {

        Pageable pageable = PageRequest.of(Page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));


        return gameRepository.findAllActive(pageable);
    }

    /*
        === 조건별 조회 ===
     */
    // 날짜 + 정렬 조회
    public Page<Game> findByCreatedAt(int page, int size, SortType sortType) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));

        return gameRepository.findAllActive(pageable);
    }

    // 이름 + 정렬 조회
    public Page<Game> findByGameName(String name, int page, int size, SortType sortType) {

        if (name == null) {
            throw new NullPointerException("name is null");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));

        return gameRepository.findActiveByName(name, pageable);
    }


    // Env + 정렬 조회
    public Page<Game> findByEnv(int page, int size, EnvType env, SortType sortType) {

        // 유효성 검사

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));

        return gameRepository.findActiveByEnvType(env, pageable);
    }

    // Tag + 정렬 조회
    public Page<Game> findByTag(int page, int size, String tag, SortType sortType) {

        // 유효성 검사
        if (StringUtils.isEmpty(tag)) {
            throw new RuntimeException("tag is empty");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));

        return gameRepository.findActiveByTag(tag, pageable);
    }

    // Price + 정렬 조회
    public Page<Game> findByPriceBetween(long minPrice, long maxPrice, int page, int size, SortType sortType) {

        // 유효성 검사
        if (minPrice == 0) {
            throw new IllegalArgumentException("minPrice is null");
        } else if (maxPrice == 0) {
            throw new IllegalArgumentException("maxPrice is null");
        }

        if (minPrice > maxPrice || minPrice == maxPrice) {
            throw new RuntimeException("minPrice or MaxPrice is less than or equal to maxPrice");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        ));

        return gameRepository.findActiveByPrice(minPrice, maxPrice, pageable);
    }
}
