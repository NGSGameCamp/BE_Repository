package com.imfine.ngs.game.entity.tag;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.util.LinkedTagId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {@link GameTag}와 {@link com.imfine.ngs.game.entity.Game}의 중간 테이블 클래스.
 *
 * @author chan
 */
@Getter
@Entity
@NoArgsConstructor
public class LinkedTag {

    // 복합키
    @EmbeddedId

    private LinkedTagId id;

    // game
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    @ManyToOne
    private Game game;

    // gameTag
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    @ManyToOne
    private GameTag gameTag;

    @Builder
    public LinkedTag(Game game, GameTag gameTag) {
        this.id = new LinkedTagId();
        this.game = game;
        this.gameTag = gameTag;
    }
}
