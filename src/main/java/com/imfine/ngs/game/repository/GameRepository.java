package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.EnvType;
import org.hibernate.metamodel.mapping.WhereRestrictable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
    Page<Game> findAllActive(Pageable pageable);

    // 게임 이름으로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Game> findActiveByName(@Param("name") String name, Pageable pageable);

    // TODO: String tag를 Tag 타입으로 변경해야한다.
    // 게임 태그로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.tag = :tag")
    Page<Game> findActiveByTag(@Param("tag") String tag, Pageable pageable);

    // TODO: String env를 EnvType
    // 게임 env로 조회
    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.env le " +
            "JOIN le.env e " +
            "WHERE g.isActive = true AND e.envType = :envType"
    )
    Page<Game> findActiveByEnvType(@Param("envType") EnvType env, Pageable pageable);

    // 게임 범위로 조회
    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.price BETWEEN  :minPrice AND :maxPrice")
    Page<Game> findActiveByPrice(long minPrice, long maxPrice, Pageable pageable);

    // 단일 게임 조회 (활성 상태만)
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.isActive = true")
    Optional<Game> findByIdAndIsActive(@Param("id") Long id);
}
