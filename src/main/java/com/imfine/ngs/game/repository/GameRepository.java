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

    // 기존 쿼리 수정 - reviews와 discounts 제외
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



    /**
     * 평균 평점이 높은 게임 목록을 조회합니다.
     * FETCH JOIN을 사용하여 N+1 문제를 방지하고, 리뷰 개수와 평균 평점 기준으로 필터링합니다.
     *
     * @param status 게임 상태 (ACTIVE, INACTIVE 등)
     * @param minReviews 최소 리뷰 개수
     * @param minScore 최소 평균 평점
     * @param pageable 페이징 정보
     * @return 조건에 맞는 게임 목록 (페이지)
     */
    @Query("SELECT DISTINCT g FROM Game g " +
            "LEFT JOIN FETCH g.publisher " +
            "LEFT JOIN FETCH g.tags t " +
            "LEFT JOIN FETCH t.gameTag " +
            "LEFT JOIN FETCH g.discounts " +
            "LEFT JOIN g.reviews r " +
            "WHERE g.gameStatus = :status " +
            "AND (r.isDeleted = false) " +
            "GROUP BY g " +
            "HAVING COUNT(r) >= :minReviews " +
            "AND AVG(CAST(r.score AS double)) >= :minScore " +
            "ORDER BY AVG(CAST(r.score AS double)) DESC")
    Page<Game> findPopularGames(
            @Param("status") GameStatusType status,
            @Param("minReviews") long minReviews,
            @Param("minScore") double minScore,
            Pageable pageable
    );

    // 단일 게임 조회 (활성 상태만)
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.gameStatus = :status")
    Optional<Game> findByIdAndGameStatus(@Param("id") Long id, @Param("status") GameStatusType status);

    // 게임 등록
    Game save(GameCreateRequest gameCreateRequest);

}
