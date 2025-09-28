package com.imfine.ngs.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityPostUpdateRequest {
  private Long boardId;

  @NotBlank(message = "제목은 필수입니다.")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  private String content;

  private List<String> tags;
}

