package com.imfine.ngs.game.entity.bundle;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.bundle.util.BundleGameListId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 번들과 게임의 중간 테이블
 * Bundle과 Game 엔티티 간의 다대다 관계를 구현
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BundleGameList {

    @EmbeddedId
    private BundleGameListId id;

    @ManyToOne
    @MapsId("bundleId")
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private Game game;

    @CreatedDate
    private LocalDateTime addedAt; // 번들에 게임이 추가된 시간

}
