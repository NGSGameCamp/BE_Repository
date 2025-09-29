package com.imfine.ngs.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommunityCommentRequest {
  @NotBlank(message = "대상 게시글이 없습니다!")
  private Long postId;

  private Long parentId;

  @NotBlank(message = "댓글 내용은 필수입니다!")
  private String content;
}
