package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityPost;
import com.imfine.ngs.community.dto.CommunityTag;
import com.imfine.ngs.community.dto.User;
import com.imfine.ngs.community.enums.SearchType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityPostService {
  void addPost(int userId, CommunityPost post) { }

  int count() { return 0; }

  public CommunityPost getPostById(int i) { return null; }

  public void editPost(int userId, int fromPostId, CommunityPost toPost) { }

  public void deletePost(int userId, int postId) { }

  public List<CommunityPost> getPostsWithSearch(SearchType type, String keyword) { return null;}

  public List<CommunityPost> getPostsWithSearch(SearchType type, List<CommunityTag> list, String keyword) { return null; }
}
