package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.repository.CommunityCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityCommentService {
  CommunityCommentRepository communityCommentRepository;

  @Autowired
  CommunityCommentService(CommunityCommentRepository communityCommentRepository) {
    this.communityCommentRepository = communityCommentRepository;
  }

  void addComment(Long userId, CommunityComment comment) { }
  int count() { return 0; }

  CommunityComment getCommentById(Long commentId) { return null; }
  public CommunityComment getCommentById(Long manager, Long id) { return null;}

  void editComment(Long userId, Long commentId, String toComment) { }

  void deleteComment(Long userId, Long commentId) { }

  public List<CommunityComment> getCommentsByPostId(Long id) { return null;}

  public List<CommunityComment> getCommentsByAuthorId(Long id) { return null;}
}
