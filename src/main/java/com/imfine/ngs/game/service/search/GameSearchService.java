package com.imfine.ngs.game.service.search;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.repository.GameRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임({@link Game}) 검색 서비스 클래스.
 * {@link Game}의 isActive가 활성화 되어야 조회가 가능하다.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
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

    // 게임 전체 조회 로직
    public List<Game> findAll(SortType sortType) {

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findAllActive(sort);
    }

    /*
        === 조건별 조회 ===
     */
    // 날짜 + 정렬 조회
    public List<Game> findByCreatedAt(SortType sortType) {

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findAllActive(sort);
    }

    // 이름 + 정렬 조회
    public List<Game> findByGameName(String name, SortType sortType) {

        if (name == null) {
            throw new NullPointerException("name is null");
        }

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findActiveByName(name, sort);
    }


    // Env + 정렬 조회
    public List<Game> findByEnv(String env, SortType sortType) {

        // 유효성 검사
        if (StringUtils.isEmpty(env)) {
            throw new RuntimeException("env is empty");
        }

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findActiveByEnvType(env, sort);
    }

    // Tag + 정렬 조회
    public List<Game> findByTag(String tag, SortType sortType) {

        // 유효성 검사
        if (StringUtils.isEmpty(tag)) {
            throw new RuntimeException("tag is empty");
        }

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findActiveByTag(tag, sort);
    }

    // Price + 정렬 조회
    public List<Game> findByPriceBetween(long minPrice, long maxPrice, SortType sortType) {

        // 유효성 검사
        if (minPrice == 0) {
            throw new IllegalArgumentException("minPrice is null");
        } else if (maxPrice == 0) {
            throw new IllegalArgumentException("maxPrice is null");
        }

        if (minPrice > maxPrice || minPrice == maxPrice) {
            throw new RuntimeException("minPrice or MaxPrice is less than or equal to maxPrice");
        }

        Sort sort = Sort.by(
                Sort.Direction.fromString(sortType.getDirection()),
                sortType.getField()
        );

        return gameRepository.findActiveByPrice(minPrice, maxPrice, sort);
    }
}
