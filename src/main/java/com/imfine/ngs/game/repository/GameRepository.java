package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.EnvType;
import org.hibernate.metamodel.mapping.WhereRestrictable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    /*
        === 메인 페이지 섹션별 조회 메서드 ===
     */
    // 활성 게임 조회 (페이지네이션용)
    Page<Game> findByIsActiveTrue(Pageable pageable);

    // 신작 게임 조회 (특정 날짜 이후 생성된 활성 게임)
    Page<Game> findByIsActiveTrueAndCreatedAtAfter(LocalDateTime date, Pageable pageable);

//    // 인기 게임 조회 (추후 구현 - viewCount 기준)
//    @Query("SELECT g FROM Game g WHERE g.isActive = true ORDER BY g.viewCount DESC")
//    Page<Game> findPopularGames(Pageable pageable);
//
//    // 할인 게임 조회 (추후 구현 - discountRate > 0)
//    @Query("SELECT g FROM Game g WHERE g.isActive = true AND g.discountRate > 0")
//    Page<Game> findDiscountedGames(Pageable pageable);

//    // N+1 문제 해결을 위한 EntityGraph 사용 메서드
//    @EntityGraph(attributePaths = {"env", "env.env"})
//    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.isActive = true")
//    Optional<Game> findByIdWithEnvironments(@Param("id") Long id);

//    @EntityGraph(attributePaths = {"env", "env.env"})
//    Page<Game> findAllWithEnvironments(Pageable pageable);
}
