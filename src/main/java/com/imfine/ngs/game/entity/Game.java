package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.entity.bundle.BundleGameList;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.notice.GameNotice;
import com.imfine.ngs.game.entity.review.Review;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 게임({@link Game}) 엔티티 클래스.
 *
 * @author chan
 */
@Table(name = "games")
@Getter
@NoArgsConstructor
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게임 식별 아이디

    @Column(nullable = false)
    private String name; // 게임 이름

    @Column(nullable = false)
    private Long price; // 게임 가격

    @OneToMany(mappedBy = "game")
    private Set<LinkedTag> tags = new HashSet<>(); // 게임 태그(ex: 액션, RPG...etc)

    @OneToMany(mappedBy = "game")
    private Set<LinkedEnv> env = new HashSet<>(); // 게임 OS

    private String thumbnailUrl; // 썸네일 이미지 URL

    private GameStatusType gameStatus; // gameStatus

    private String description; // 게임 설명

    private String spec; // 게임 사양

    @CreatedDate
    private LocalDateTime createdAt; // 게임 등록 날짜

    @CreatedDate
    private LocalDateTime updatedAt; // 게임 업데이트 날짜

    @OneToMany(mappedBy = "game")
    private Set<BundleGameList> bundles = new HashSet<>(); // 번들 관계

    @OneToMany(mappedBy = "game")
    private List<SingleGameDiscount> discounts = new ArrayList<>(); // 할인 정보

    @OneToMany(mappedBy = "game")
    private List<Review> reviews = new ArrayList<>(); // 리뷰

    @OneToMany(mappedBy = "game")
    private List<GameNotice> notices = new ArrayList<>(); // 공지사항

    @Builder
    public Game(Long id, String name, Long price, Set<LinkedTag> tags, Set<LinkedEnv> env, String thumbnailUrl, GameStatusType gameStatus, String description, String spec, LocalDateTime createdAt, LocalDateTime updatedAt, Set<BundleGameList> bundles, List<SingleGameDiscount> discounts, List<Review> reviews, List<GameNotice> notices) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tags = tags;
        this.env = env;
        this.thumbnailUrl = thumbnailUrl;
        this.gameStatus = gameStatus;
        this.description = description;
        this.spec = spec;
        this.bundles = bundles;
        this.discounts = discounts;
        this.reviews = reviews;
        this.notices = notices;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
}

