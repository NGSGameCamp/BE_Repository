package com.imfine.ngs.game.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.repository.tag.LinkedTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkedTagService {
    private final LinkedTagRepository linkedTagRepository;

    public LinkedTag createLinkedTag(GameTag gameTag, Game game) {
        return linkedTagRepository.save(LinkedTag.builder()
                .game(game)
                .gameTag(gameTag)
                .build());
    }

}
