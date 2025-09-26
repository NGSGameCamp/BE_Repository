package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.entity.env.LinkedEnv;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 게임({@link Game}) 엔티티 클래스.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
 *
 * @author chan
 */
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게임 식별 아이디

    @Column(nullable = false)
    private String name; // 게임 이름

    @Column(nullable = false)
    private Long price; // 게임 가격

    // TODO: GameTag로 변경
    private String tag; // 게임 태그(ex: 액션, RPG...etc)

    @Builder.Default
    @OneToMany(mappedBy = "game")
    private Set<LinkedEnv> env = new HashSet<>(); // 게임 OS

    private String thumbnailURL; // 썸네일 이미지 URL

    // TODO: GameStatus 테이블의 id를 가져와야한다. -> private Long gameStatusId
    private boolean isActive; // gameStatus

    // TODO: Publisher 테이블의 id를 가져와야한다.
//    private Long publisherId;

    private String introduction; // 게임 소개

    private String spec; // 게임 사양

    @CreatedDate
    private LocalDateTime createdAt; // 게임 등록 날짜

    @CreatedDate
    private LocalDateTime updatedAt; // 게임 업데이트 날짜

}

