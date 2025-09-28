package com.imfine.ngs.community.controller;

import com.imfine.ngs.community.dto.response.CommunityCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {
  // TODO: 댓글 추가
  public ResponseEntity<Void> createComment() {
    return null;
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
