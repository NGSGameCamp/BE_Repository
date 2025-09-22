package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.CommunityCommentRepository;
import com.imfine.ngs.community.repository.TestUserRepository;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
  private final CommunityCommentRepository commentRepo;
  private final CommunityPostService postService;
  private final TestUserRepository userRepo;
  private final ValidationService valid;

  /* TODO: comment Builder 쓸 때 author 추가하도록 하기.
   *  addComment의 userId 인자 제거
   */
  Long addComment(Long userId, CommunityComment comment) {
    comment.setAuthorId(userId);

    if (comment.getContent().isBlank())
      throw new IllegalArgumentException("내용이 없습니다!");
    if (postService.getPostById(comment.getPostId()) == null)
      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    // comment.getParentId()가 -1이 아니고, commentRepo에서 부모 댓글을 찾을 수 없다면 잘못된 접근
    if (comment.getParentId() != -1L && commentRepo.findById(comment.getParentId()).orElse(null) == null)
      throw new IllegalArgumentException("유효하지 않은 접근입니다!");

    return commentRepo.save(comment).getId();
  }
  Long count() {
    return commentRepo.count();
  }

  CommunityComment getCommentById(Long commentId) {
    return getCommentById(null, commentId);
  }
  public CommunityComment getCommentById(Long managerId, Long commentId) {
    // TODO: 이거 지우고 PreAuthorize 사용
    TestUser tmpUser = null;
    if (managerId != null)
      tmpUser = userRepo.findById(managerId).orElse(null);

    if (commentId == null)
      throw new IllegalArgumentException("인자가 잘못되었습니다!");
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);

    if (comment == null)
      throw new IllegalArgumentException("유효하지 않은 댓글입니다!");
    if (comment.getIsDeleted() && !valid.isValidUser(comment.getAuthorId(), tmpUser))
      throw new IllegalArgumentException("접근 권한이 없습니다!");
    if (postService.getPostById(comment.getPostId()).getIsDeleted() && valid.isValidUser(comment.getAuthorId(), tmpUser))
      return comment;
    if (postService.getPostById(comment.getPostId()).getIsDeleted() && !valid.isValidUser(comment.getAuthorId(), tmpUser))
      throw new IllegalArgumentException("접근 권한이 없습니다!");
//      return comment;

    return comment;
  }

  Long editComment(Long userId, Long commentId, String toComment) {
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);
    TestUser tmpUser = userRepo.findById(userId).orElse(null);

    if (!valid.isValidUser(comment.getAuthorId(), tmpUser))
      throw new IllegalArgumentException("접근 권한이 없습니다!");
    if (toComment.isBlank())
      throw new IllegalArgumentException("내용이 없습니다!");

    comment.updateContent(toComment);
    return commentRepo.save(comment).getId();
  }

  void deleteComment(Long userId, Long commentId) {
    CommunityComment comment = commentRepo.findById(commentId).orElse(null);
    TestUser tmpUser = userRepo.findById(userId).orElse(null);

    if (comment == null) throw new IllegalArgumentException("유효하지 않은 댓글입니다!");
    if (valid.isValidUser(comment.getAuthorId(), tmpUser)) {
      comment.setIsDeleted(true);
      commentRepo.save(comment);
    }
  }

  /*
   * TODO: 특정 권한을 가질 때 조건 추가
   *  테스트 추가 필요?
   */
  public List<CommunityComment> getCommentsByPostId(Long postId) {
    return commentRepo.getCommunityCommentsByPostId(postId);
  }

  public List<CommunityComment> getCommentsByAuthorId(Long userId) {
    return commentRepo.getCommunityCommentsByAuthorId(userId);
  }
}
