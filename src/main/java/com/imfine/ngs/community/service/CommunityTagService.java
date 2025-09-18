package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.repository.CommunityTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityTagService {
  CommunityTagRepository communityTagRepository;

  @Autowired
  CommunityTagService(CommunityTagRepository communityTagRepository) {
    this.communityTagRepository = communityTagRepository;
  }

  public CommunityTag getTagByName(String tagName) {

    return null;
  }

  public List<CommunityTag> getTagsByName(String tmpText) { return null;}
}
