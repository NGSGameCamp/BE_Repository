package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityTag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityTagServiceTest {

  private final CommunityTagService tagService;

  @Autowired
  CommunityTagServiceTest (
          CommunityTagService communityTagService
  ) {
    this.tagService = communityTagService;
  }

  @BeforeEach
  void setUp() {

  }

  @Test
  @DisplayName("언제든 태그는 주기. 없으면 만들어서라도")
  void getTagAnyTime() {
    // Given
    String tagName = "가이드";

    // When
    CommunityTag tmp = tagService.getTagByName(tagName);

    // Then
    assertThat(tmp).isNotNull();
    assertThat(tmp.getName()).isEqualTo(tagName);
  }

  @Test
  @DisplayName("해당 단어를 포함하는 모든 태그를 출력해야 함")
  void getTagsThatAreSimilar() {
    // Given
    String tmpText = "플랫";

    // When
    List<CommunityTag> tags = tagService.getTagsByName(tmpText);

    // Then
    assertThat(tags).allMatch(tag -> tag.getName().contains(tmpText));
  }
}
