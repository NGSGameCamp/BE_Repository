package com.imfine.ngs.community.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.controller.mapper.CommunityMapper;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityCommentRequest;
import com.imfine.ngs.community.dto.response.CommunityCommentResponse;
import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.service.CommunityCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {
  private final CommunityMapper mapper;
  private final CommunityCommentService commentService;

  /**
   * 댓글을 작성합니다.
   * @param postId
   * @param principal
   * @param request
   * @return
   */
  @PostMapping("/comment/{postId}")
  public ResponseEntity<Void> createComment(
          @PathVariable Long postId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestBody @Valid CommunityCommentRequest request
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);
    request.setPostId(postId);
    request.setAuthor(user);
    CommunityComment comment = mapper.toComment(request);

    try {
      commentService.addComment(user, comment);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 게시글 별로 댓글을 조회합니다.
   * @param postId
   * @param principal
   * @return
   */
  @GetMapping("/comment/{postId}")
  public ResponseEntity<List<CommunityCommentResponse>> getCommentsByPostId(
          @PathVariable Long postId,
          @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser user = mapper.getCommunityUserOrAnonymous(principal);

    List<CommunityComment> comments = commentService.getCommentsByPostId(user, postId);

    try {
      return ResponseEntity.ok(mapper.toCommentResponses(comments));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 작성자 별로 댓글을 조회합니다.
   * Pagination이 적용됩니다.
   * @param userId
   * @param page
   * @param size
   * @return
   */
  @GetMapping("/comment/user/{userId}")
  public ResponseEntity<Page<CommunityCommentResponse>> getCommentsByAuthorId(
          @PathVariable Long userId,
          @RequestParam(value = "size", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "100") int size
  ) {
    try {
      Page<CommunityComment> comments = commentService.getCommentsByAuthorId(userId, page, size);
      Page<CommunityCommentResponse> responses = mapper.toCommentResponses(comments);
      return ResponseEntity.ok(responses);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 댓글을 수정합니다.
   * @param commentId
   * @param principal
   * @param content
   * @return
   */
  @PatchMapping("/comment/{commentId}")
  public ResponseEntity<Void> updateComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestBody String content
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);

    try {
      commentService.editComment(user, commentId, content);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 댓글을 삭제처리 합니다.
   * @param commentId
   * @param principal
   * @return
   */
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<Void> deleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);

    try {
      commentService.deleteComment(user, commentId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }
}
