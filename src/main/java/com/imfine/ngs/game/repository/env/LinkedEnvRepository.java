package com.imfine.ngs.game.repository.env;

import com.imfine.ngs.game.entity.env.LinkedEnv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link LinkedEnv} 저장소 인터페이스.
 *
 * @author chan
 */
public interface LinkedEnvRepository extends JpaRepository<LinkedEnv, Long> {

    List<LinkedEnv> findByGame_Id(Long gameId);
}
