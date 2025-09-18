package com.imfine.ngs.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
  int id;
  String name;
  @Builder.Default String role = "USER";
}
