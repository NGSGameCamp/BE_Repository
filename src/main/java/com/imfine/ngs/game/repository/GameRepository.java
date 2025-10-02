package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
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


    @Query("SELECT g.id FROM Game g " +
            "LEFT JOIN g.reviews r " +
            "WHERE g.gameStatus.statusType = :status " +
            "AND (r.isDeleted = false OR r.isDeleted IS NULL) " +
            "GROUP BY g.id " +
            "HAVING COUNT(r) >= :minReviews " +
            "AND AVG(CAST(r.score AS double)) >= :minScore " +
            "ORDER BY AVG(CAST(r.score AS double)) DESC")
    Page<Long> findPopularGameIds(
            @Param("status") GameStatusType status,
            @Param("minReviews") long minReviews,
            @Param("minScore") double minScore,
            Pageable pageable
    );

    // 단일 게임 조회 (활성 상태만)
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.gameStatus.statusType = :statusType")
    Optional<Game> findByIdAndGameStatus(@Param("id") Long id, @Param("statusType") GameStatusType statusType);

    // 게임 등록
    Game save(GameCreateRequest gameCreateRequest);

//  List<Game> findGamesBy(List<Long> content);
}
