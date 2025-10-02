package com.imfine.ngs.game.service.support;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.repository.env.LinkedEnvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkedEnvService {

    private final LinkedEnvRepository envRepository;
    private final LinkedEnvRepository linkedEnvRepository;

    public LinkedEnv createLinkedEnv(Env env, Game game) {
        return envRepository.save(LinkedEnv.builder()
                .env(env)
                .game(game)
                .build());
    }

    public void createLinkedEnvs(List<Env> envs, Game game) {
       envs.forEach(env -> linkedEnvRepository.save(
               LinkedEnv.builder()
                .env(env)
                .game(game)
                .build())
       );
    }
}
