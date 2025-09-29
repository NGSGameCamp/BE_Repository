package com.imfine.ngs.game.entity.bundle.util;

import com.imfine.ngs.game.entity.bundle.BundleGameList;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link BundleGameList}의 복합키
 */
@Getter
@Embeddable
public class BundleGameListId implements Serializable {

    private Long bundleId;
    private Long gameId;

    // 복합키는 반드시 equals와 hashCode를 구현해야 한다.

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BundleGameListId)) {
            return false;
        }

        BundleGameListId that = (BundleGameListId) obj;

        return Objects.equals(bundleId, that.bundleId) && Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundleId, gameId);
    }
}