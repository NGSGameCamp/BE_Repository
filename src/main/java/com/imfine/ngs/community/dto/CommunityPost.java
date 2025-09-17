package com.imfine.ngs.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class CommunityPost {
  int id;
  int boardId;
  int authorId;
  String title;
  String content;
  @Setter
  List<CommunityTag> tags;
}
