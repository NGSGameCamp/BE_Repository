package com.imfine.ngs.community;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommunityComment {
  private int id;
  private int postId;
  @Builder.Default
  private int parentId = -1;
  private int authorId;
  private String content;
}
