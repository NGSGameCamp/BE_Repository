package com.imfine.ngs.game.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 게임({@link Game}의 공지사항 엔티티 클래스.
 * 현재는 간소화된 상태이다.
 *
 * @author chan
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameNotice {

    // id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // game
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    // title
    private String title;

    // content
    private String content;

    // category
    // TODO: NoticeCategory를 사용해야한다.
    private String category;

    // createdAt
    @CreatedDate
    private LocalDateTime createdAt;

    // deletedAt
    @CreatedDate
    private LocalDateTime deletedAt;
}
