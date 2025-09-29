package com.imfine.ngs.community.dto;

import com.imfine.ngs.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityUser {
  private Long id;

  private String nickname;

  @Builder.Default
  private String role = "USER";

  public static CommunityUser of(User user) {
    return CommunityUser.builder()
            .id(user.getId())
            .nickname(user.getName())
            .role(user.getRole().getRole())
            .build();
  }
}
