package com.imfine.ngs.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommunityBoardManagerDto {
  @NotBlank(message = "대상 유저를 선택하십시오!")
  public Long managerId;
}
