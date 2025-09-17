package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityBoard;
import com.imfine.ngs.community.dto.CommunityComment;
import com.imfine.ngs.community.dto.CommunityPost;
import com.imfine.ngs.community.dto.User;
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
public class CommunityCommentServiceTest {
  CommunityCommentService commentService;
  CommunityPostService postService;
  CommunityBoardService boardService;
  TestUserService userService;

  @Autowired
  public CommunityCommentServiceTest(
          CommunityCommentService commentService,
          CommunityPostService postService,
          CommunityBoardService boardService,
          TestUserService userService) {

    this.commentService = commentService;
    this.postService = postService;
    this.boardService = boardService;
    this.userService = userService;
  }

  // 테스트 전 준비해야하는 데이터들 나열
  @BeforeEach
  void setUp() {
    // 매니저 생성
    User manager = User.builder()
            .id(427)
            .name("Manager")
            .role("MANAGER")
            .build();
    userService.addUser(manager);
    // 보드 생성
    for (int i = 0; i < 5; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("This is test board for post test" + i)
              .build();

      boardService.addBoard(board);
    }
    // 유저 5명 생성
    for (int i = 0 ; i < 5; ++i) {
      User user = User.builder()
              .id(i)
              .name("communityTest"+i)
              .build();

      userService.addUser(user);
    }
    // 게시글 5개 생성
    for (int i = 0 ; i < 5; ++i) {
      CommunityPost post = CommunityPost.builder()
              .boardId(1)
              .title("commentTestPost"+1)
              .content("this is test post for comment test" + i)
              .build();

      postService.addPost((int)(Math.random() * 5), post);
    }
    // 댓글 30개 생성
    for (int i = 0 ; i < 30; ++i) {
      CommunityComment comment = CommunityComment.builder()
              .postId((int)(Math.random() * 5))
              .content("this is test comment for comment test" + i)
              .build();

      commentService.addComment((int)(Math.random() * 5), comment);
    }
  }

  // 댓글 작성 파트
  // ============================================================
  @Test
  @DisplayName("유효하지 않은 유저가 댓글을 작성할 경우 댓글 수가 늘어나면 안 됨")
  void addCommentWithWrongUser() {
    // Given
    User user = User.builder()
            .id(50)
            .name("WrongUser")
            .build();
    CommunityComment comment = CommunityComment.builder()
            .postId(2)
            .content("test comment")
            .build();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user.getId(), comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt);
  }

  @Test
  @DisplayName("댓글 내용이 없으면 댓글 수가 늘어나면 안 됨")
  void addCommentWithNoContent() {
    // Given
    User user = userService.getUserById(2);
    CommunityComment comment = CommunityComment.builder()
            .postId(2)
            .authorId(user.getId())
            .content("")
            .build();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user.getId(), comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt);
  }

  @Test
  @DisplayName("부모 댓글이 올바르지 않으면 댓글 수가 늘어나면 안 됨")
  void addCommentWithWrongParent() {
    // Given
    User user = userService.getUserById(2);
    CommunityComment comment = CommunityComment.builder()
            .postId(2)
            .parentId(50)
            .content("this is test comment")
            .build();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user.getId(), comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt);
  }

  @Test
  @DisplayName("올바르지 않은 게시글에 댓글을 작성하면 댓글 수가 늘어나면 안 됨")
  void addCommentOnWrongPost() {
    // Given
    User user = userService.getUserById(2);
    CommunityComment comment = CommunityComment.builder()
            .postId(50000)
            .content("this is test comment")
            .build();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user.getId(), comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt);
  }

  @Test
  @DisplayName("올바른 댓글을 작성하면 댓글 수가 늘어나야 함")
  void addCommentOnRightCondition() {
    User user = userService.getUserById(2);
    CommunityComment comment = CommunityComment.builder()
            .postId(1)
            .content("this is test comment")
            .build();
    int commentCnt = commentService.count();

    // When
    commentService.addComment(user.getId(), comment);

    // Then
    assertThat(commentService.count()).isEqualTo(commentCnt+1);
  }

  // 댓글 수정 파트
  // ============================================================
  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 수정 시도할 경우 그 내용이 바뀌면 안 됨")
  void editCommentWithWrongUser() {
    // Given
    CommunityComment comment = commentService.getCommentById(2);
    User user = userService.getUserById(
            comment.getAuthorId() > 1 ? comment.getAuthorId()-1 : comment.getAuthorId()+1);
    String toContent = "이걸로 바뀜";

    int tmpId = comment.getId();

    // When
    commentService.editComment(user.getId(), comment.getId(), toContent);

    // Then
    assertThat(commentService.getCommentById(tmpId))
            .isEqualTo(comment);
  }

  @Test
  @DisplayName("댓글을 수정했을 때 내용이 없으면 그 내용이 바뀌면 안 됨")
  void editCommentWithNoContent() {
    // Given
    CommunityComment comment = commentService.getCommentById(2);
    User user = userService.getUserById(comment.getAuthorId());
    String toContent = "";

    int tmpId = comment.getId();

    // When
    commentService.editComment(user.getId(), comment.getId(), toContent);

    // Then
    assertThat(commentService.getCommentById(tmpId))
            .isEqualTo(comment);
  }

  // 댓글 삭제 파트
  // ============================================================
  @Test
  @DisplayName("알맞지 않은 유저가 댓글을 삭제 시도할 경우 댓글 수가 줄어들면 안 됨")
  void deleteCommentWithWrongUser() {
    // Given
    CommunityComment comment = commentService.getCommentById(2);
    User user = userService.getUserById(
            comment.getAuthorId() > 1 ? comment.getAuthorId()-1 : comment.getAuthorId()+1);

    // int tmpId = comment.getId();
    int tmp = commentService.count();


    // When
    commentService.deleteComment(user.getId(), comment.getId());

    // Then
    // assertThat(commentService.getCommentById(tmpId)).isEqualTo(comment);
    assertThat(commentService.count()).isEqualTo(tmp);
  }

  @Test
  @DisplayName("알맞은 유저가 댓글을 삭제 시도할 경우 댓글 수가 줄어들어야 함")
  void deleteCommentOnRightCondition() {
    // Given
    CommunityComment comment = commentService.getCommentById(2);
    User user = userService.getUserById(comment.getAuthorId());
    int tmp = commentService.count();

    // When
    commentService.deleteComment(user.getId(), comment.getId());

    // Then
    assertThat(commentService.count()).isEqualTo(tmp -1);
  }

    // TODO: 관리자가 댓글 제거하는 경우?

  // 댓글 조회 파트
  // ============================================================
  @Test
  @DisplayName("유효하지 않은 댓글을 조회할 때 null을 출력함")
  void getCommentWithWrongCommentId() {
    // When
    CommunityComment comment = commentService.getCommentById(21231);

    // Then
    assertThat(comment).isNull();
  }

  @Test
  @DisplayName("대상 댓글을 갖는 게시글이 유효하지 않을 때 null을 출력함")
  void getCommentWithInvalidPost() {
    // Given
    User user = userService.getUserById(1);
    CommunityPost post = CommunityPost.builder()
            .id(100)
            .boardId(1)
            .authorId(user.getId())
            .title("댓글 테스트용 포스트")
            .content("댓글 테스트용 포스트 입니다.")
            .build();
    CommunityComment comment = CommunityComment.builder()
            .id(500)
            .postId(post.getId())
            .authorId(user.getId())
            .content("테스트용 댓글이에요")
            .build();
    postService.addPost(user.getId(), post);
    commentService.addComment(user.getId(), comment);
    postService.deletePost(user.getId(), post.getId());

    // When
    CommunityComment tmp = commentService.getCommentById(comment.getId());

    // Then
    assertThat(tmp).isNull();
  }

  @Test
  @DisplayName("유효한 댓글을 조회할 때 대상 댓글을 출력함")
  void getCommentOnRightCondition() {
    // When
    CommunityComment comment = commentService.getCommentById(2);

    // Then
    assertThat(comment).isNotNull();
  }

  @Test
  @DisplayName("관리자가 댓글 조회 할 경우 유효하지 않은 게시글의 댓글도 조회함")
  void getCommentWithManager() {
    // Given
    User user = userService.getUserById(1);
    CommunityPost post = CommunityPost.builder()
            .id(100)
            .boardId(1)
            .authorId(user.getId())
            .title("댓글 테스트용 포스트")
            .content("댓글 테스트용 포스트 입니다.")
            .build();
    CommunityComment comment = CommunityComment.builder()
            .id(500)
            .postId(post.getId())
            .authorId(user.getId())
            .content("테스트용 댓글이에요")
            .build();
    postService.addPost(user.getId(), post);
    commentService.addComment(user.getId(), comment);
    postService.deletePost(user.getId(), post.getId());

    User manager = userService.getUserByName("Manager");

    // When
    CommunityComment tmp = commentService.getCommentById(manager, comment.getId());

    // Then
    assertThat(tmp).isNull();
  }
    // 여러 개 조회
      //
  @Test
  @DisplayName("원하는 Post의 모든 Comment 가져오게 하기")
  void getCommentsOnSpecificPost() {
    // Given
    CommunityPost post = postService.getPostById(1);

    // When
    List<CommunityComment> comments = commentService.getCommentsByPostId(post.getId());

    // Then
    assertThat(comments).allMatch(comment -> comment.getPostId() == post.getId());
  }
      //
  @Test
  @DisplayName("원하는 Author의 모든 Comment 가져오게 하기")
  void getCommentsOnSpecificAuthor() {
    // Given
    User user = userService.getUserById(1);

    // When
    List<CommunityComment> comments = commentService.getCommentsByAuthorId(user.getId());

    // Then
    assertThat(comments).allMatch(comment -> comment.getAuthorId() == user.getId());
  }
}
