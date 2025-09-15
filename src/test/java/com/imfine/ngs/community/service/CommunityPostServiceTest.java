package com.imfine.ngs.community.service;

import com.imfine.ngs.community.CommunityPost;
import com.imfine.ngs.community.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommunityPostServiceTest {
  // 게시글 작성 파트
  // ============================================================
  @Test
  @DisplayName("글 작성 시 내용이 없으면 작성된 글 개수가 늘어나면 안 됨.")
  void writePostWithNoContent() {
    // Given: 준비
    CommunityPostService postService = new CommunityPostService();
    User user = new User();
    CommunityPost post = new CommunityPost();
    int postCnt = postService.count();

    // When: 실행
    postService.addPost(user, post);

    // Then: 검증
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("글 작성 시 유저가 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨")
  void writePostWithWrongUserId() {
    // Given
    CommunityPostService postService = new CommunityPostService();
    User user = new User();
    CommunityPost post = new CommunityPost();
    int postCnt = postService.count();

    // When
    postService.addPost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt);
  }
}
