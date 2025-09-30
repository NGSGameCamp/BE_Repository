package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.entity.bundle.BundleGameList;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
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
@AllArgsConstructor
@Builder
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게임 식별 아이디

    private String name; // 게임 이름
    private Long price; // 게임 가격
    private String description; // 게임 설명
    private String spec; // 게임 사양

    @OneToMany(mappedBy = "game")
    @Builder.Default
    private Set<LinkedTag> tags = new HashSet<>(); // 게임 태그(ex: 액션, RPG...etc)

    @OneToMany(mappedBy = "game")
    @Builder.Default
    private Set<LinkedEnv> env = new HashSet<>(); // 게임 OS

    private String thumbnailUrl; // 썸네일 이미지 URL

    private GameStatusType gameStatus; // gameStatus

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

}

