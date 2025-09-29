package com.imfine.ngs.community.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class CommunityBoardResponse {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
