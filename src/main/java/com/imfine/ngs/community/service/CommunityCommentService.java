package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.repository.CommunityCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
  private final CommunityCommentRepository commentRepository;
  private final CommunityPostService postService;
  private final CommunityBoardService boardService;

  public Long addComment(CommunityUser user, CommunityComment comment) {
    if (comment.getContent() == null || comment.getContent().isBlank())
      throw new IllegalArgumentException("내용이 없습니다!");
    if (postService.getPostById(user, comment.getPostId()) == null)
      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    if (comment.getParentId() != -1L && commentRepository.findById(comment.getParentId()).orElse(null) == null)
      throw new IllegalArgumentException("유효하지 않은 대댓글입니다!");

    return commentRepository.save(comment).getId();
  }

  public Long countAll() {
    return commentRepository.count();
  }

  public Long count(Long postId) {
    return commentRepository.countByPostId(postId);
  }

  public CommunityComment getCommentById(CommunityUser user, Long commentId) {
    CommunityComment comment = commentRepository.findById(commentId).orElse(null);
    if (comment == null) throw new IllegalArgumentException("유효하지 않은 댓글입니다!");

    return switch (user.getRole()) {
      case "USER" -> {
        CommunityPost post = postService.getPostById(user, comment.getPostId());
        CommunityBoard board = boardService.getBoardById(user, post.getBoardId());

        if ((!board.getManagerId().equals(user.getId()))
                && ((board.getIsDeleted() || post.getIsDeleted()) || comment.getIsDeleted()))
          throw new IllegalArgumentException("잘못된 접근입니다!");

        yield comment;
      }
      case "MANAGER" -> comment;
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  public CommunityComment getCommentById(Long commentId) {
    CommunityUser tmpUser = CommunityUser.builder()
            .nickname("tmpUSer")
            .build();
    return getCommentById(tmpUser, commentId);
  }

  @Transactional(rollbackOn = Exception.class)
  public Long editComment(CommunityUser user, Long commentId, String toContent) {
    CommunityComment comment = getCommentById(user, commentId);
    comment.updateContent(toContent);

    if (!comment.getAuthorId().equals(user.getId()) || comment.getIsDeleted())
      throw new IllegalArgumentException("수정 권한이 없습니다!");

    if (comment.getContent() == null || comment.getContent().isBlank())
      throw new IllegalArgumentException("내용이 없습니다!");

    return commentRepository.save(comment).getId();
  }

  //  void deleteComment(Long userId, Long commentId) {
//    CommunityComment comment = commentRepository.findById(commentId).orElse(null);
//    CommunityUser tmpUser = userRepo.findById(userId).orElse(null);
//
//    if (comment == null) throw new IllegalArgumentException("유효하지 않은 댓글입니다!");
//    if (valid.isValidUser(comment.getAuthorId(), tmpUser)) {
//      comment.setIsDeleted(true);
//      commentRepository.save(comment);
//    }
//  }
  void deleteComment(CommunityUser user, Long commentId) {
    CommunityComment comment = getCommentById(user, commentId);
    if (comment == null) throw new IllegalArgumentException("유효하지 않은 댓글입니다!");

    switch (user.getRole()) {
      case "USER" -> {
        CommunityPost post = postService.getPostById(user, comment.getPostId());
        CommunityBoard board = boardService.getBoardById(user, post.getBoardId());

        if (!comment.getAuthorId().equals(user.getId()) && !board.getManagerId().equals(user.getId()))
          throw new IllegalArgumentException("삭제 권한이 없습니다!");

        if (comment.getIsDeleted())
          throw new IllegalArgumentException("유효하지 않은 댓글입니다!");
      }
      case "MANAGER" -> {
      }
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    }

    comment.setIsDeleted(true);
    commentRepository.save(comment);
  }

  public List<CommunityComment> getCommentsByPostId(CommunityUser user, Long postId) {
    return switch (user.getRole()) {
      case "USER" -> {
        CommunityPost post = postService.getPostById(user, postId);
        CommunityBoard board = boardService.getBoardById(user, post.getBoardId());

        if (board.getManagerId().equals(user.getId()))
          yield commentRepository.findCommunityCommentsByPostId(postId, true);

        yield commentRepository.findCommunityCommentsByPostId(postId, false);
      }
      case "MANAGER" -> commentRepository.findCommunityCommentsByPostId(postId, true);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  /* TODO: comment는 page를 어떻게 구현할까?
   *  프론트에서 알아서 처리하기?
   */
  public List<CommunityComment> getCommentsByPostId(Long postId) {
    CommunityUser tmpUser = CommunityUser.builder().build();

    return getCommentsByPostId(tmpUser, postId);
  }


  public Page<CommunityComment> getCommentsByAuthorId(Long userId) {
    Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt");
    return commentRepository.findCommunityCommentsByAuthorId(userId, pageable);
  }
}
