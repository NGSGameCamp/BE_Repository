package com.imfine.ngs.community;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityComment {
  private int id;
  private String content;
  private int userId;

  public boolean isExist() { return true; }
}
