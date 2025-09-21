package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.repository.CommunityTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityTagService {
  private final CommunityTagRepository tagRepository;

  public CommunityTag addTag(String tagName) {
    CommunityTag tag = CommunityTag.builder()
            .name(tagName)
            .build();
    return tagRepository.save(tag);
  }

  public CommunityTag getTagByName(String tagName) {
    CommunityTag result = tagRepository.findByName(tagName).orElse(null);
    if (result == null) {
      result = addTag(tagName);
    }

    return result;
  }

  public List<CommunityTag> getTagsByName(String tmpText) {
    return tagRepository.findAllByName(tmpText);
  }
}
