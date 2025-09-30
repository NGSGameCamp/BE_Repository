package com.imfine.ngs.game.entity.tag;

import com.imfine.ngs.game.enums.GameTagType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game}에서 사용할 {@link GameTagType} 엔티티 클래스.
 *
 * @author chan
 */
@Getter
@Entity
@RequiredArgsConstructor
public class GameTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameTagType tagType;

    @Builder
    public GameTag(GameTagType tagType) {
        this.tagType = tagType;
    }
}
