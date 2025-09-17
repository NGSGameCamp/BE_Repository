package com.imfine.ngs.game.config;

import com.imfine.ngs.game.entity.Env;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.repository.EnvRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게임({@link com.imfine.ngs.game.entity.Game} 도메인 설정 클래스.
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GameConfig {

    private final EnvRepository envRepository;

    @PostConstruct
    @Transactional
    public void envInit() {

        log.debug("Initializing Env data...");

        int created = 0;
        for (EnvType type : EnvType.values()) {
            if (!envRepository.existsByEnvType(type)) {

                Env env = new Env();
                env.setEnvType(type);
                envRepository.save(env);
                created++;
                log.debug("Created Env Data: {}", type);
            }
        }

        log.debug("Env initialization completed. Created: {}", created);
    }
}
