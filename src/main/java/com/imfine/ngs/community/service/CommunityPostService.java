package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.repository.CommunityPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityPostService {
  CommunityPostRepository communityPostRepository;

  @Autowired
  CommunityPostService(CommunityPostRepository communityPostRepository) {
    this.communityPostRepository = communityPostRepository;
  }

  void addPost(Long userId, CommunityPost post) { }

  public int count() { return 0; }

  public CommunityPost getPostById(Long i) { return null; }

  public void editPost(Long userId, Long fromPostId, CommunityPost toPost) { }

  public void deletePost(Long userId, Long postId) { }

  public List<CommunityPost> getPostsWithSearch(SearchType type, String keyword) { return null;}

  public List<CommunityPost> getPostsWithSearch(SearchType type, List<CommunityTag> list, String keyword) { return null; }
}
