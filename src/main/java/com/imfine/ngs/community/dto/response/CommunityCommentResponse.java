package com.imfine.ngs.community.dto.response;

import com.imfine.ngs.community.dto.CommunityUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityCommentResponse {
  private Long parentId;

  private CommunityUser user;

  private String content;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
