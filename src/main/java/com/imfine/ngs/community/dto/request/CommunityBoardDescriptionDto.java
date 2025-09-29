package com.imfine.ngs.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommunityBoardDescriptionDto {
  @NotBlank(message = "설명을 작성해주세요!")
  private String description;
}
