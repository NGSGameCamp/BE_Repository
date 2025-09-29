package com.imfine.ngs.game.entity.tag.util;

import com.imfine.ngs.game.entity.tag.LinkedTag;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link LinkedTag}의 복합키
 */
@Getter
@Embeddable
public class LinkedTagId implements Serializable {

    private Long gameId;
    private Long tagId;

    // equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LinkedTagId)) {
            return false;
        }

        LinkedTagId that = (LinkedTagId) obj;

        return Objects.equals(gameId, that.gameId) && Objects.equals(tagId, that.tagId);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(gameId, tagId);
    }
}
