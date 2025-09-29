package com.imfine.ngs.community.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.controller.mapper.CommunityMapper;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityCommentRequest;
import com.imfine.ngs.community.dto.response.CommunityCommentResponse;
import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {
  private final CommunityMapper mapper;
  private final CommunityCommentService commentService;

  // TODO: 댓글 추가
  @PostMapping("/comment/{postId}")
  public ResponseEntity<Void> createComment(
          @PathVariable Long postId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          CommunityCommentRequest request
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);
    CommunityComment comment = mapper.toCommunityComment(request);

    try {
      commentService.addComment(user, comment);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  // TODO: 댓글 조회 (게시글 별)
  public ResponseEntity<List<CommunityCommentResponse>> getCommentsByPostId() {
    return null;
  }

  // TODO: 댓글 조회 (작성자 별)
  public ResponseEntity<List<CommunityCommentResponse>> getCommentsByAuthorId() {
    return null;
  }

  // TODO: 댓글 수정
  public ResponseEntity<Void> updateComment() {
    return null;
  }

  // TODO: 댓글 삭제
  public ResponseEntity<Void> deleteComment() {
    return null;
  }
}
