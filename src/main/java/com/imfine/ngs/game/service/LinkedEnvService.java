package com.imfine.ngs.game.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.repository.env.LinkedEnvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkedEnvService {

    private final LinkedEnvRepository envRepository;

    public LinkedEnv createLinkedEnv(Env env, Game game) {
        return envRepository.save(LinkedEnv.builder()
                .env(env)
                .game(game)
                .build());
    }
}
