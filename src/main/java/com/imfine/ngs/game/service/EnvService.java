package com.imfine.ngs.game.service;

import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.repository.env.EnvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvService {
    private final EnvRepository envRepository;

    public Env findByEnvType(EnvType envType) {
        return envRepository.findByEnvType(envType)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ENV 입니다. : " + envType));
    }
}
