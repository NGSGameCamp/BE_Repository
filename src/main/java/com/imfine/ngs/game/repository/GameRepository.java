package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
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

    // 기존 findGameDetailById 제거하고 새 메서드 추가
    // TODO: [fix-149] 부분적 n + 1 문제와 카데시안 곱 문제가 남아있다.
    @Query("SELECT DISTINCT g FROM Game g " +
            "LEFT JOIN FETCH g.tags t " +
            "LEFT JOIN FETCH t.gameTag " +
            "LEFT JOIN FETCH g.env e " +
            "LEFT JOIN FETCH e.env " +
            "LEFT JOIN FETCH g.publisher " +
            "WHERE g.id = :id")
    Optional<Game> findByIdWithDetails(@Param("id") Long id);

    // reviews 별도 조회 메서드 추가
    @Query("SELECT r FROM Review r " +
            "WHERE r.game.id = :gameId " +
            "AND r.isDeleted = false " +
            "ORDER BY r.createdAt DESC")
    List<Review> findActiveReviewsByGameId(@Param("gameId") Long gameId);

    // discounts 별도 조회 메서드 추가
    @Query("SELECT d FROM SingleGameDiscount d " +
            "WHERE d.game.id = :gameId " +
            "AND (d.expiresAt IS NULL OR d.expiresAt > CURRENT_TIMESTAMP) " +
            "ORDER BY d.discountRate DESC")
    List<SingleGameDiscount> findActiveDiscountsByGameId(@Param("gameId") Long gameId);

    /*
        === 활성화 여부로 게임 조회 가능
     */
    // 전체 활성 게임 조회 (동적 정렬 메서드)
    @Query("SELECT g FROM Game g WHERE g.gameStatus = :status")
    Page<Game> findAllActive(@Param("status") GameStatusType status, Pageable pageable);

    // 게임 이름으로 조회
    @Query("SELECT g FROM Game g WHERE g.gameStatus = :status AND LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Game> findActiveByName(@Param("status") GameStatusType status, @Param("name") String name, Pageable pageable);

    // 게임 태그로 조회 (LinkedTag를 통한 조회)
    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.tags lt " +
            "JOIN lt.gameTag gt " +
            "WHERE g.gameStatus = :status AND gt.tagType = :tagType")
    Page<Game> findActiveByTag(@Param("status") GameStatusType status, @Param("tagType") GameTagType tagType, Pageable pageable);

    // 게임 env로 조회
    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.env le " +
            "JOIN le.env e " +
            "WHERE g.gameStatus = :status AND e.envType = :envType"
    )
    Page<Game> findActiveByEnvType(@Param("status") GameStatusType status, @Param("envType") EnvType env, Pageable pageable);

    // 게임 범위로 조회
    @Query("SELECT g FROM Game g WHERE g.gameStatus = :status AND g.price BETWEEN  :minPrice AND :maxPrice")
    Page<Game> findActiveByPrice(@Param("status") GameStatusType status, long minPrice, long maxPrice, Pageable pageable);

    // 단일 게임 조회 (활성 상태만)
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.gameStatus = :status")
    Optional<Game> findByIdAndGameStatus(@Param("id") Long id, @Param("status") GameStatusType status);

    /*
        === 메인 페이지 섹션별 조회 메서드 ===
     */
    // 활성 게임 조회 (페이지네이션용)
    Page<Game> findByGameStatus(GameStatusType gameStatus, Pageable pageable);

    // 신작 게임 조회 (특정 날짜 이후 생성된 활성 게임)
    Page<Game> findByGameStatusAndCreatedAtAfter(GameStatusType gameStatus, LocalDateTime date, Pageable pageable);

    // 게임 등록
    Game save(GameCreateRequest gameCreateRequest);
}
