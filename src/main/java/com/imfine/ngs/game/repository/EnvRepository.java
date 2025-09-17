package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.Env;
import com.imfine.ngs.game.enums.EnvType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link Env} 저장소 인터페이스.
 *
 * @author chan
 */
public interface EnvRepository extends JpaRepository<Env, Long> {

    boolean existsByEnvType(EnvType envType);

    Optional<Env> findByEnvType(EnvType envType);
}
