package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityComment;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.dto.CommunityUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityCommentServiceTest {
  CommunityCommentService commentService;
  CommunityPostService postService;
  CommunityBoardService boardService;

  @Autowired
  public CommunityCommentServiceTest(
          CommunityCommentService commentService,
          CommunityPostService postService,
          CommunityBoardService boardService) {
    this.commentService = commentService;
    this.postService = postService;
    this.boardService = boardService;
  }

  Long postId;
  Long boardId;
  Long commentId;
  CommunityUser manager;
  CommunityUser boardManager;
  CommunityUser correctUser;
  CommunityUser wrongUser;

  // 테스트 전 준비해야하는 데이터들 나열
  @BeforeEach
  void setUp() {
    // 유저 생성
    manager = CommunityUser.builder()
            .id(1L)
            .nickname("Manager")
            .role("MANAGER")
            .build();
    boardManager = CommunityUser.builder()
            .id(2L)
            .nickname("Board Manager")
            .build();
    correctUser = CommunityUser.builder()
            .id((long) (Math.random() * 123126673))
            .nickname("Correct User")
            .build();
    wrongUser = CommunityUser.builder()
            .id((long) (Math.random() * 12568954))
            .nickname("Wrong User")
            .build();

    // 보드 생성
    for (int i = 0; i < 5; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("This is test board for post test" + i)
              .gameId((long) (Math.random() * 100000))
              .managerId(boardManager.getId())
              .build();

      Long tmp = boardService.addBoard(board);

      if (i == 2)
        boardId = tmp;
    }

    // 게시글 5개 생성
    for (int i = 0; i < 5; ++i) {
      CommunityPost post = CommunityPost.builder()
              .boardId(boardId)
              .authorId(boardManager.getId())
              .title("commentTestPost" + 1)
              .content("this is test post for comment test" + i)
              .build();

      Long tmp = postService.addPost(correctUser, post);

      if (i == 3)
        postId = tmp;
    }
    // 댓글 30개 생성
    for (int i = 0; i < 30; ++i) {
      CommunityComment comment = CommunityComment.builder()
              .postId(postId)
              .authorId(correctUser.getId())
              .content("this is test comment for comment test" + i)
              .build();

      Long tmp = commentService.addComment(correctUser, comment);
      if (i == 3)
        commentId = tmp;
    }
  }

  // 댓글 작성 파트
  // ============================================================
  @Test
  @DisplayName("댓글 내용이 없으면 에러")
  void addCommentWithNoContent() {
    CommunityComment comment = CommunityComment.builder()
            .postId(postId)
            .authorId(correctUser.getId())
            .content("")
            .build();

    // When
    assertThatThrownBy(() -> commentService.addComment(correctUser, comment))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("부모 댓글이 올바르지 않으면 에러")
  void addCommentWithWrongParent() {
    // Given
    CommunityComment comment = CommunityComment.builder()
            .postId(postId)
            .authorId(correctUser.getId())
            .parentId(1023023L)
            .content("this is test comment")
            .build();

    // When & Then
    assertThatThrownBy(() -> commentService.addComment(correctUser, comment))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바르지 않은 게시글에 댓글을 작성하면 에러")
  void addCommentOnWrongPost() {
    // Given
    CommunityComment comment = CommunityComment.builder()
            .postId(50000L)
            .content("this is test comment")
            .build();

    // When
    assertThatThrownBy(() -> commentService.addComment(correctUser, comment))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바른 댓글을 작성하면 댓글 수가 늘어나야 함")
  void addCommentOnRightCondition() {
    // Given
    CommunityComment comment = CommunityComment.builder()
            .postId(postId)
            .authorId(wrongUser.getId())
            .parentId(commentId)
            .content("this is test comment")
            .build();
    Long commentCnt = commentService.countAll();

    // When
    commentService.addComment(wrongUser, comment);

    // Then
    assertThat(commentService.countAll()).isEqualTo(commentCnt + 1);
  }

  // 댓글 수정 파트
  // ============================================================
  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 수정 시도할 경우 그 내용이 바뀌면 안 됨")
  void editCommentWithWrongUser() {
    // Given
    String toContent = "이걸로 바뀔거임";

    // When
    assertThatThrownBy(() -> commentService.editComment(wrongUser, commentId, toContent))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("댓글을 수정했을 때 내용이 없으면 그 내용이 바뀌면 안 됨")
  void editCommentWithNoContent() {
    // Given
    String toContent = "";

    // When
    assertThatThrownBy(() -> commentService.editComment(correctUser, commentId, toContent))
            .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> commentService.editComment(correctUser, commentId, null))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바르게 댓글 수정하면 내용이 바뀌어야 함")
  void editCommentOnRightCondition() {
    // Given
    String toContent = "이걸로 바뀔거임";

    // When
    Long tmp = commentService.editComment(correctUser, commentId, toContent);

    assertThat(commentService.getCommentById(tmp).getContent()).isEqualTo(toContent);
  }

  // 댓글 삭제 파트
  // ============================================================
  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 삭제 시도할 경우 에러")
  void deleteCommentWithWrongUser() {
    // When & Then
    assertThatThrownBy(() -> commentService.deleteComment(wrongUser, commentId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("알맞은 유저가 댓글을 삭제 시도할 경우 해당 댓글의 isDeleted가 true가 되어야 함")
  void deleteCommentOnRightCondition() {
    // When
    commentService.deleteComment(correctUser, commentId);

    // Then
    assertThat(commentService.getCommentById(boardManager, commentId).getIsDeleted()).isTrue();
  }

  @Test
  @DisplayName("관리자 또는 게시판 담당자가 댓글을 삭제 시도할 경우 해당 댓글의 isDeleted가 true가 되어야 함")
  void deleteCommentWithManager() {
    // When
    commentService.deleteComment(manager, commentId);

    // Then
    assertThat(commentService.getCommentById(boardManager, commentId).getIsDeleted()).isTrue();
  }

  // 댓글 조회 파트
  // ============================================================
  @Test
  @DisplayName("유효하지 않은 댓글을 조회하면 에러")
  void getCommentWithWrongCommentId() {
    // When
    assertThatThrownBy(() -> commentService.getCommentById(21231L))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("대상 댓글을 갖는 게시글이 유효하지 않으면 에러")
  void getCommentWithInvalidPost() {
    // Given
    postService.deletePost(manager, postId);

    // When
    assertThatThrownBy(() -> commentService.getCommentById(commentId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("유효한 댓글을 조회할 때 대상 댓글을 출력함")
  void getCommentOnRightCondition() {
    // When
    CommunityComment comment = commentService.getCommentById(commentId);

    // Then
    assertThat(comment).isNotNull();
  }

  @Test
  @DisplayName("관리자가 댓글 조회 할 경우 유효하지 않은 게시글의 댓글도 조회함")
  void getCommentWithManager() {
    // Given
    postService.deletePost(manager, boardId);

    // When
    CommunityComment tmp = commentService.getCommentById(manager, commentId);

    // Then
    assertThat(tmp).isNotNull();
  }

  // 여러 개 조회
  //
  @Test
  @DisplayName("원하는 Post의 모든 Comment 가져오게 하기")
  void getCommentsOnSpecificPost() {
    // Given
    CommunityPost post = postService.getPostById(postId);

    // When
    List<CommunityComment> comments = commentService.getCommentsByPostId(post.getId());

    // Then
    assertThat(comments).allMatch(comment -> comment.getPostId().equals(post.getId()));
  }

  //
  @Test
  @DisplayName("원하는 Author의 모든 Comment 가져오게 하기")
  void getCommentsOnSpecificAuthor() {
    // When
    List<CommunityComment> comments = commentService.getCommentsByAuthorId(correctUser.getId()).getContent();

    // Then
    assertThat(comments).allMatch(comment -> comment.getAuthorId().equals(correctUser.getId()));
  }
}
