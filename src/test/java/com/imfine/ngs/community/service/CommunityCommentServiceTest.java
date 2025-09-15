package com.imfine.ngs.community.service;

import com.imfine.ngs.community.CommunityComment;
import com.imfine.ngs.community.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class CommunityCommentServiceTest {
  @Test
  @DisplayName("유효하지 않은 유저가 댓글을 작성할 경우 댓글 수가 늘어나면 안 됨")
  void addCommentWithWrongUser() {
    // Given
    CommunityCommentService commentService = new CommunityCommentService();
    CommunityComment comment = new CommunityComment();
    User user = new User();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user, comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt);
  }

  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 편집할 경우 그 내용이 바뀌면 안 됨")
  void editCommentWithWrongUser() {
    // Given
    CommunityCommentService commentService = new CommunityCommentService();
    // todo: 예시 내용 추가해야 함
    CommunityComment comment = new CommunityComment();
    String toContent = "이걸로 바뀜";
    User user = new User();
    int tmpId = comment.getId();

    // When
    commentService.editComment(user, comment, toContent);

    // Then
    assertThat(commentService.getCommentById(tmpId))
            .isEqualTo(comment);
  }

  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 제거 시도할 경우 댓글이 제거되면 안 됨")
  void deleteCommentWithWrongUser() {
    // Given
    CommunityCommentService commentService = new CommunityCommentService();
    // todo: 예시 내용 추가해야 함
    CommunityComment comment = new CommunityComment();
    User user = new User();
    int tmpId = comment.getId();

    // When
    commentService.deleteComment(user, comment);

    // Then
    assertThat(commentService.getCommentById(tmpId)).isEqualTo(comment);
  }
}
