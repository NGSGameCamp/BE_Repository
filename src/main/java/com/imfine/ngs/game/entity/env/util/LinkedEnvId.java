package com.imfine.ngs.game.entity.env.util;

import com.imfine.ngs.game.entity.env.LinkedEnv;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link LinkedEnv}의 복합키
 */
@Getter
@Setter
@Embeddable
public class LinkedEnvId implements Serializable {

    private Long gameId;
    private Long envId;

    // 복합키는 반드시 equals와 hashCode 구현해아한다.

    // equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LinkedEnvId)) {
            return false;
        }

        LinkedEnvId that = (LinkedEnvId) obj;

        return Objects.equals(gameId, that.gameId) && Objects.equals(envId, that.envId);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(gameId, envId);
    }
}
