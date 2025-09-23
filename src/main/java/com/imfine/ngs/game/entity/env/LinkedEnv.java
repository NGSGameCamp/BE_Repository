package com.imfine.ngs.game.entity.env;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.util.LinkedEnvId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link Env}와 {@link com.imfine.ngs.game.entity.Game}의 중간 테이블 클래스.
 *
 * @author chan
 */
@Getter
@Setter
@Table(name = "linked_env")
@Entity
public class LinkedEnv {

    // 복합키
    @EmbeddedId
    private LinkedEnvId id;

    // Game
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    @ManyToOne
    private Game game;

    // Env
    @MapsId("envId")
    @JoinColumn(name = "env_id")
    @ManyToOne
    private Env env;
}
