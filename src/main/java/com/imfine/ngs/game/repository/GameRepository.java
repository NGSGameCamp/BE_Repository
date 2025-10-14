package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.dto.request.GameCreateRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.queryDsl.GameRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link Game} 저장소 인터페이스.
 * TODO: 현재 하나의 리포지토리에서 너무 많은 걸 담당하고 있다. 차후 엔티티별로 분리할 필요가 있다.
 *
 * Spring Data JPA 기본 기능 + QueryDSL 커스텀 기능을 모두 제공합니다.
 *
 * @author chan
 */
public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {

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

    // 평점과 리뷰로 게임 조회 (평점 3.5 이상, 리뷰 50개 이상)
    @Query("SELECT DISTINCT g FROM Game g " +
            "LEFT JOIN FETCH g.publisher " +
            "WHERE g.gameStatus = :status " +
            "  AND (SELECT AVG(r.score) FROM Review r WHERE r.game = g AND r.isDeleted = false) >= 3.5 " +
            "  AND (SELECT COUNT(r) FROM Review r WHERE r.game = g AND r.isDeleted = false) >= 50 " +
            "ORDER BY " +
            "  (SELECT AVG(r.score) FROM Review r WHERE r.game = g AND r.isDeleted = false) DESC NULLS LAST, " +
            "  (SELECT COUNT(r) FROM Review r WHERE r.game = g AND r.isDeleted = false) DESC, " +
            "  g.createdAt DESC")
    Page<Game> findRecommendedGame(@Param("status") GameStatusType status, Pageable pageable);


    // 단일 게임 조회 (활성 상태만)
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.gameStatus = :status")
    Optional<Game> findByIdAndGameStatus(@Param("id") Long id, @Param("status") GameStatusType status);

    // 게임 등록
    Game save(GameCreateRequest gameCreateRequest);

    // 태그로 게임 조회 (모든 태그를 포함하는 게임만 조회)
    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.tags lt " +
            "JOIN lt.gameTag gt " +
            "WHERE g.gameStatus = :status " +
            "  AND gt.tagType IN :tagTypes " +
            "GROUP BY g.id " +
            "HAVING COUNT(DISTINCT gt.tagType) = :tagCount " +
            "ORDER BY g.createdAt DESC")
    Page<Game> findByTagsAndStatus(
            @Param("tagTypes") List<GameTagType> tagTypes,
            @Param("tagCount") long tagCount,
            @Param("status") GameStatusType status,
            Pageable pageable
    );

    @Query("SELECT distinct g from Game g " +
            "where g.gameStatus = 0 " +
            "order by g.createdAt DESC")
    Page<Game> findAllRelease(Pageable pageable);

    // 특정 날짜 이후에 출시된 게임 조회
    @Query("SELECT DISTINCT g FROM Game g " +
            "WHERE g.gameStatus = 0 " +
            "  AND g.createdAt >= :startDate " +
            "ORDER BY g.createdAt DESC")
    Page<Game> findReleasedAfter(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
    // 게임 이름으로 검색 (부분 일치, 대소문자 무시)
    @Query("SELECT DISTINCT g FROM Game g " +
            "LEFT JOIN FETCH g.publisher " +
            "WHERE g.gameStatus = :status " +
            "  AND LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY g.createdAt DESC")
    Page<Game> findByGameTitle(
            @Param("name") String name,
            @Param("status") GameStatusType status,
            Pageable pageable
    );
  
    // 평균 평점 범위로 게임 조회
    @Query("SELECT DISTINCT g FROM Game g " +
            "WHERE g.gameStatus = :status " +
            "  AND (SELECT AVG(r.score) FROM Review r WHERE r.game = g AND r.isDeleted = false) >= :minAverage " +
            "  AND (SELECT AVG(r.score) FROM Review r WHERE r.game = g AND r.isDeleted = false) <= :maxAverage " +
            "ORDER BY " +
            "  (SELECT AVG(r.score) FROM Review r WHERE r.game = g AND r.isDeleted = false) DESC NULLS LAST, " +
            "  g.createdAt DESC")
    Page<Game> findByAverageScore(
            @Param("minAverage") Double minAverage,
            @Param("maxAverage") Double maxAverage,
            @Param("status") GameStatusType status,
            Pageable pageable
    );
  
    // 모든 게임 가격 오름차순 조회
    @Query("SELECT DISTINCT g FROM Game g WHERE g.gameStatus = 0 ORDER BY g.price ASC")
    Page<Game> findAllByPriceOrder(Pageable pageable);

    // 가격 범위로 게임 조회
    @Query("SELECT DISTINCT g FROM Game g " +
            "WHERE g.gameStatus = :status " +
            "  AND g.price >= :minPrice " +
            "  AND g.price <= :maxPrice " +
            "ORDER BY g.price ASC")
    Page<Game> findByPriceRange(
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("status") GameStatusType status,
            Pageable pageable
    );

    // 우선순위 검색용 후보 게임 조회 (태그 OR 조건)
    @Query("SELECT DISTINCT g FROM Game g " +
            "LEFT JOIN FETCH g.publisher " +
            "LEFT JOIN FETCH g.tags lt " +
            "LEFT JOIN FETCH lt.gameTag gt " +
            "WHERE g.gameStatus = :status " +
            "  AND (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "  AND (:minPrice IS NULL OR g.price >= :minPrice) " +
            "  AND (:maxPrice IS NULL OR g.price <= :maxPrice) " +
            "  AND gt.tagType IN :tags")
    List<Game> findCandidateGamesForPrioritySearch(
            @Param("name") String name,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("tags") List<GameTagType> tags,
            @Param("status") GameStatusType status
    );

    /**
     * 우선순위 검색 최적화: DB에서 태그 매칭 점수 계산 및 정렬 수행
     *
     * @param tagTypes 검색할 태그 타입 리스트 (ENUM 값)
     * @param status 게임 상태
     * @param name 게임 이름 (선택적)
     * @param minPrice 최소 가격 (선택적)
     * @param maxPrice 최대 가격 (선택적)
     * @param pageable 페이징 정보
     * @return 태그 매칭 점수순으로 정렬된 게임 페이지
     */
    @Query(value = """
        SELECT g.*, COUNT(DISTINCT gt.tag_type) as match_score
        FROM game g
        INNER JOIN linked_tag lt ON g.id = lt.game_id
        INNER JOIN game_tag gt ON lt.tag_id = gt.id
        WHERE g.game_status = :status
          AND gt.tag_type IN :tagTypes
          AND (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:minPrice IS NULL OR g.price >= :minPrice)
          AND (:maxPrice IS NULL OR g.price <= :maxPrice)
        GROUP BY g.id
        HAVING match_score > 0
        ORDER BY match_score DESC, g.created_at DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT g.id)
        FROM game g
        INNER JOIN linked_tag lt ON g.id = lt.game_id
        INNER JOIN game_tag gt ON lt.tag_id = gt.id
        WHERE g.game_status = :status
          AND gt.tag_type IN :tagTypes
          AND (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:minPrice IS NULL OR g.price >= :minPrice)
          AND (:maxPrice IS NULL OR g.price <= :maxPrice)
        GROUP BY g.id
        HAVING COUNT(DISTINCT gt.tag_type) > 0
        """,
        nativeQuery = true)
    Page<Game> searchGamesWithPriorityOptimized(
            @Param("tagTypes") List<String> tagTypes,
            @Param("status") int status,
            @Param("name") String name,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            Pageable pageable
    );

//  List<Game> findGamesBy(List<Long> content);
}
