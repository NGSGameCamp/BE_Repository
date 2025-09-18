package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityComment;
import com.imfine.ngs.community.dto.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityCommentService {
  void addComment(int userId, CommunityComment comment) { }
  int count() { return 0; }

  CommunityComment getCommentById(int commentId) { return null; }
  public CommunityComment getCommentById(User manager, int id) { return null;}

  void editComment(int userId, int commentId, String toComment) { }

  void deleteComment(int userId, int commentId) { }

  public List<CommunityComment> getCommentsByPostId(int id) { return null;}

  public List<CommunityComment> getCommentsByAuthorId(int id) { return null;}
}
