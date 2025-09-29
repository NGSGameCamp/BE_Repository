package com.imfine.ngs.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommunityBoardCreateRequest {
  @NotBlank(message = "대상 게임을 지정해야합니다!")
  Long gameId;

  @NotBlank(message = "제목은 필수입니다!")
  String title;

  String description;
}
