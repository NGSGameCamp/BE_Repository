package com.imfine.ngs.community.service;

import com.imfine.ngs.community.CommunityPost;
import com.imfine.ngs.community.User;
import org.springframework.stereotype.Service;

@Service
public class CommunityPostService {
  void addPost(User user, CommunityPost post) { }

  int count() { return 0; }

  public CommunityPost getPostById(int i) { return null; }

  public void editPost(User user, CommunityPost fromPost, CommunityPost toPost) { }

  public void deletePost(User user, CommunityPost post) { }

  public void deletePostById(User user, int id) { }
}
