package com.imfine.ngs.game.repository.tag;

import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.enums.GameTagType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link GameTag} 저장소 인터페이스.
 *
 * @author chan
 */
@Repository
public interface GameTagRepository extends JpaRepository<GameTag, Long> {

    boolean existsByTagType(GameTagType gameTagType);

    Optional<GameTag> findByTagType(GameTagType tagType);
}
