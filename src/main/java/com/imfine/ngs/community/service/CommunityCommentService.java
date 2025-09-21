package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.CommunityCommentRepository;
import com.imfine.ngs.community.repository.TestUserRepository;
import com.imfine.ngs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityCommentService {
  CommunityCommentRepository commentRepo;
  CommunityPostService postService;
  TestUserRepository userRepo;
  ValidationService valid;

  @Autowired
  CommunityCommentService(
          CommunityCommentRepository communityCommentRepository,
          CommunityPostService postService,
          TestUserRepository userRepository) {
    this.commentRepo = communityCommentRepository;
    this.postService = postService;
    this.userRepo = userRepository;
    this.valid = new ValidationService();
  }

  Long addComment(Long userId, CommunityComment comment) {
    comment.setAuthorId(userId);

    if (comment.getContent().isBlank())
      return null;
    if (postService.getPostById(comment.getPostId()) == null)
      return null;

    // comment.getParentId()가 -1이 아니고, commentRepo에서 부모 댓글을 찾을 수 없다면 null
    if (comment.getParentId() != -1L && commentRepo.findById(comment.getParentId()).orElse(null) == null)
      return null;

    return commentRepo.save(comment).getId();
  }
  Long count() {
    return commentRepo.count();
  }

  CommunityComment getCommentById(Long commentId) {
    return getCommentById(null, commentId);
  }
  public CommunityComment getCommentById(Long managerId, Long commentId) {
    TestUser tmpUser = null;
    if (managerId != null)
      tmpUser = userRepo.findById(managerId).orElse(null);

    if (commentId == null)
      return null;
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);

    if (comment == null)
      return null;
    if (comment.getIsDeleted() && !valid.isValidUser(comment.getAuthorId(), tmpUser))
      return null;
    if (postService.getPostById(comment.getPostId()) == null)
      return null;

    return comment;
  }

  Long editComment(Long userId, Long commentId, String toComment) {
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);
    TestUser tmpUser = userRepo.findById(userId).orElse(null);

    if (valid.isValidUser(comment.getAuthorId(), tmpUser))
      return commentId;
    if (toComment.isBlank())
      return commentId;

    comment.updateContent(toComment);
    return commentRepo.save(comment).getId();
  }

  void deleteComment(Long userId, Long commentId) {
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);
    TestUser tmpUser = userRepo.findById(userId).orElse(null);

    if (valid.isValidUser(comment.getAuthorId(), tmpUser)) {
      comment.setIsDeleted(true);
      commentRepo.save(comment);
    }
  }

  public List<CommunityComment> getCommentsByPostId(Long postId) {
    return commentRepo.getCommunityCommentsByPostId(postId);
  }

  public List<CommunityComment> getCommentsByAuthorId(Long userId) {
    return commentRepo.getCommunityCommentsByAuthorId(userId);
  }
}
