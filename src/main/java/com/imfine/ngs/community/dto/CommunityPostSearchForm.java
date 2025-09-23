package com.imfine.ngs.community.dto;

import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.enums.SearchType;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Builder
public class CommunityPostSearchForm {
  @Builder.Default
  public final SearchType type = SearchType.TITLE_ONLY;
  @Builder.Default
  public final String keyword = "";
  @Builder.Default
  public final List<CommunityTag> tagList = new ArrayList<>();
  @Builder.Default
  public final Pageable pageable = PageRequest.of(0, 30, Sort.Direction.DESC, "created_at");
}
