package com.imfine.ngs.game.service.support;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.request.GameTagRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.tag.GameTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameTagService {

    private final GameTagRepository gameTagRepository;

    public void createTag(GameTagRequest gameTagRequest) {
        gameTagRepository.save(GameTag.builder()
                .tagType(gameTagRequest.getGameTagType())
                .build());
    }

    public GameTag findByTagId(long tagId) {
        return gameTagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("해당 태그는 존재하지 않습니다. tagId : " + tagId));
    }

    public GameTag findByTagType(GameTagType gameTagType) {
        return gameTagRepository.findByTagType(gameTagType)
                .orElseThrow(() -> new IllegalArgumentException("해당 태그는 존재하지 않습니다." + gameTagType));
    }

    public List<GameTag> findByGameTagTypes(List<GameTagRequest> gameTagRequest) {
        return gameTagRequest.stream()
                .map(this::getGameTagOrThrow)
                .toList();
    }

    private GameTag getGameTagOrThrow(GameTagRequest tagRequest) {
        return gameTagRepository.findByTagType(tagRequest.getGameTagType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 태그는 존재하지 않습니다: " + tagRequest.getGameTagType()));
    }
}
