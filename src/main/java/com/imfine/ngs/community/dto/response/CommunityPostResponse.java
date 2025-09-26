package com.imfine.ngs.community.dto.response;

import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.entity.CommunityPost;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityPostResponse {

  private final Long id;
  private final Long boardId;
  private final CommunityUser author;
  private final String title;
  private final String content;
  private final Boolean deleted;
  private final List<String> tags;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static CommunityPostResponse from(CommunityPost post, CommunityUser author) {
    List<String> tagNames = post.getTags() == null
        ? Collections.emptyList()
        : post.getTags().stream()
            .filter(Objects::nonNull)
            .map(tag -> tag.getName())
            .collect(Collectors.toList());

    return CommunityPostResponse.builder()
        .id(post.getId())
        .boardId(post.getBoardId())
        .author(author)
        .title(post.getTitle())
        .content(post.getContent())
        .deleted(post.getIsDeleted())
        .tags(tagNames)
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }
}
