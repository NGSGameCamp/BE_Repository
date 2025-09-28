package com.imfine.ngs.community.dto.request;

import lombok.Getter;

@Getter
public class CommunityBoardCreateRequest {
  Long gameId;
  String title;
  String description;
}
