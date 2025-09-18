package com.imfine.ngs.community.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommunityBoard {
  int id;
  int manager_id;
  String title;
  String description;
  @Builder.Default boolean isActive = true;
}
