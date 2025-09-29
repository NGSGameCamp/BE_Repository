package com.imfine.ngs.game.entity.notice;

import com.imfine.ngs.game.entity.Game;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 게임({@link Game}의 공지사항 엔티티 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private NoticeCategory category;

    // createdAt
    @CreatedDate
    private LocalDateTime createdAt;

    // deletedAt
    @CreatedDate
    private LocalDateTime deletedAt;
}
