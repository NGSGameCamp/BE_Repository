package com.imfine.ngs.game.repository.tag;

import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.entity.tag.util.LinkedTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link LinkedTag} 저장소 인터페이스.
 *
 * @author chan
 */
public interface LinkedTagRepository extends JpaRepository<LinkedTag, LinkedTagId> {

    List<LinkedTag> findByGame_Id(Long gameId);
    LinkedTag save(GameTag gameTag);
}
