package com.imfine.ngs.community;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommunityBoard {
  int id;
  String title;
  String description;
  @Builder.Default boolean isActive = true;
}
