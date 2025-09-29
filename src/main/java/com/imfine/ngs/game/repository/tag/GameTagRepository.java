package com.imfine.ngs.game.repository.tag;

import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.enums.GameTagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link GameTag} 저장소 인터페이스.
 *
 * @author chan
 */
public interface GameTagRepository extends JpaRepository<GameTag, Long> {

    boolean existsByTagType(GameTagType gameTagType);

    Optional<GameTag> findByTagType(GameTagType gameTagType);
}
