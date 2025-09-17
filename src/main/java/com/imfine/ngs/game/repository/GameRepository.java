package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.Game;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * {@link Game} 저장소 인터페이스.
 *
 * @author chan
 */
public interface GameRepository extends JpaRepository<Game, Long> {

    /*
        === 활성화 여부로 게임 조회 가능
     */
    // 전체 활성 게임 조회 (동적 정렬 메서드)
    @Query("SELECT g FROM Game g WHERE g.isActive = true")
    List<Game> findAllActive(Sort sort);

    // 게임 이름으로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Game> findActiveByName(@Param("name") String name, Sort sort);

    // TODO: String tag를 Tag 타입으로 변경해야한다.
    // 게임 태그로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.tag = :tag")
    List<Game> findActiveByTag(@Param("tag") String tag, Sort sort);

    // TODO: String env를 EnvType
    // 게임 env로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.env = :env")
    List<Game> findActiveByEnvType(@Param("env") String env, Sort sort);

    // 게임 범위로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.price BETWEEN  :minPrice AND :maxPrice")
    List<Game> findActiveByPrice(long minPrice, long maxPrice, Sort sort);
}
