package com.imfine.ngs.community.dto.request;

import com.imfine.ngs.community.dto.CommunityUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommunityCommentRequest {
  @Setter
  private CommunityUser author;

  @Setter
  private Long postId;

  private Long parentId;

  @NotBlank(message = "댓글 내용은 필수입니다!")
  private String content;
}
