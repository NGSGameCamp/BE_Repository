package com.imfine.ngs.game.entity.env;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.enums.EnvType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 게임({@link Game})에서 사용할 {@link EnvType} 관리 엔티티 클래스.
 *
 * @author chan
 */
@Getter
@Entity
public class Env {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnvType envType;
}
