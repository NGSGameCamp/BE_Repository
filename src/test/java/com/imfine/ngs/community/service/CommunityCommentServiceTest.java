//package com.imfine.ngs.community.service;
//
//import com.imfine.ngs.community.entity.CommunityBoard;
//import com.imfine.ngs.community.entity.CommunityComment;
//import com.imfine.ngs.community.entity.CommunityPost;
//import com.imfine.ngs.community.dto.CommunityUser;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@Transactional
//@ActiveProfiles("test")
//@SpringBootTest
//public class CommunityCommentServiceTest {
//  CommunityCommentService commentService;
//  CommunityPostService postService;
//  CommunityBoardService boardService;
//  TestUserService userService;
//
//  @Autowired
//  public CommunityCommentServiceTest(
//          CommunityCommentService commentService,
//          CommunityPostService postService,
//          CommunityBoardService boardService,
//          TestUserService userService) {
//    this.commentService = commentService;
//    this.postService = postService;
//    this.boardService = boardService;
//    this.userService = userService;
//  }
//
//  Long managerId;
//  Long postId;
//  Long boardId;
//  Long userId;
//  Long commentId;
//
//  // 테스트 전 준비해야하는 데이터들 나열
//  @BeforeEach
//  void setUp() {
//    // 매니저 생성
//    CommunityUser manager = CommunityUser.builder()
//            .name("Manager")
//            .role("MANAGER")
//            .build();
//    managerId = userService.addUser(manager);
//    // 보드 생성
//    for (int i = 0; i < 5; ++i) {
//      CommunityBoard board = CommunityBoard.builder()
//              .title("This is test board for post test" + i)
//              .gameId((long) (Math.random()*100000))
//              .managerId(managerId)
//              .build();
//
//      Long tmp = boardService.addBoard(board);
//
//      if (i == 2)
//        boardId = tmp;
//    }
//    // 유저 5명 생성
//    for (int i = 0 ; i < 5; ++i) {
//      CommunityUser user = CommunityUser.builder()
//              .name("communityTest"+i)
//              .build();
//
//      Long tmp = userService.addUser(user);
//
//      if (i == 3)
//        userId = tmp;
//    }
//    // 게시글 5개 생성
//    for (int i = 0 ; i < 5; ++i) {
//      CommunityPost post = CommunityPost.builder()
//              .boardId(boardId)
//              .title("commentTestPost"+1)
//              .content("this is test post for comment test" + i)
//              .build();
//
//      Long tmp = postService.addPost(userId, post);
//
//      if (i == 3)
//        postId = tmp;
//    }
//    // 댓글 30개 생성
//    for (int i = 0 ; i < 30; ++i) {
//      CommunityComment comment = CommunityComment.builder()
//              .postId(postId)
//              .content("this is test comment for comment test" + i)
//              .build();
//
//      Long tmp = commentService.addComment(userId, comment);
//      if (i == 3)
//        commentId = tmp;
//    }
//  }
//
//  // 댓글 작성 파트
//  // ============================================================
//  @Test
//  @DisplayName("댓글 내용이 없으면 댓글 수가 늘어나면 안 됨")
//  void addCommentWithNoContent() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//
//    CommunityComment comment = CommunityComment.builder()
//            .postId(postId)
//            .authorId(userId)
//            .content("")
//            .build();
//    Long commentCnt = commentService.count();
//
//    // When
//    assertThatThrownBy(() -> commentService.addComment(user.getId(), comment))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("부모 댓글이 올바르지 않으면 댓글 수가 늘어나면 안 됨")
//  void addCommentWithWrongParent() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//    CommunityComment comment = CommunityComment.builder()
//            .postId(postId)
//            .parentId(1023023L)
//            .content("this is test comment")
//            .build();
//    Long commentCnt = commentService.count();
//
//    // When
//    assertThatThrownBy(() -> commentService.addComment(user.getId(), comment))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("올바르지 않은 게시글에 댓글을 작성하면 댓글 수가 늘어나면 안 됨")
//  void addCommentOnWrongPost() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//    CommunityComment comment = CommunityComment.builder()
//            .postId(50000L)
//            .content("this is test comment")
//            .build();
//
//    // When
//    assertThatThrownBy(() -> commentService.addComment(user.getId(), comment))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("올바른 댓글을 작성하면 댓글 수가 늘어나야 함")
//  void addCommentOnRightCondition() {
//    CommunityUser user = userService.getUserById(userId);
//    CommunityComment comment = CommunityComment.builder()
//            .postId(postId)
//            .content("this is test comment")
//            .build();
//    Long commentCnt = commentService.count();
//
//    // When
//    commentService.addComment(user.getId(), comment);
//
//    // Then
//    assertThat(commentService.count()).isEqualTo(commentCnt+1);
//  }
//
//  // 댓글 수정 파트
//  // ============================================================
//  @Test
//  @DisplayName("알맞지 않은 유저가 댓글을 수정 시도할 경우 그 내용이 바뀌면 안 됨")
//  void editCommentWithWrongUser() {
//    // Given
//    CommunityComment comment = commentService.getCommentById(commentId);
//    CommunityUser user = userService.getUserById(comment.getAuthorId()+1);
//    String toContent = "이걸로 바뀜";
//
//    // When
//    assertThatThrownBy(() -> commentService.editComment(user.getId(), comment.getId(), toContent))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("댓글을 수정했을 때 내용이 없으면 그 내용이 바뀌면 안 됨")
//  void editCommentWithNoContent() {
//    // Given
//    CommunityComment comment = commentService.getCommentById(commentId);
//    CommunityUser user = userService.getUserById(comment.getAuthorId());
//    String toContent = "";
//
//    Long tmpId = comment.getId();
//
//    // When
//    assertThatThrownBy(() -> commentService.editComment(user.getId(), comment.getId(), toContent))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  // 댓글 삭제 파트
//  // ============================================================
//  @Test
//  @DisplayName("알맞지 않은 유저가 댓글을 삭제 시도할 경우 댓글 수가 줄어들면 안 됨")
//  void deleteCommentWithWrongUser() {
//    // Given
//    CommunityComment comment = commentService.getCommentById(commentId);
//    CommunityUser user = userService.getUserById(comment.getAuthorId());
//
//    // int tmpId = comment.getId();
//    Long tmp = commentService.count();
//
//
//    // When
//    commentService.deleteComment(user.getId(), comment.getId());
//
//    // Then
//    assertThat(commentService.count()).isEqualTo(tmp);
//  }
//
//  @Test
//  @DisplayName("알맞은 유저가 댓글을 삭제 시도할 경우 해당 댓글의 isDeleted가 true가 되어야 함")
//  void deleteCommentOnRightCondition() {
//    // Given
//    CommunityComment comment = commentService.getCommentById(commentId);
//    CommunityUser user = userService.getUserById(comment.getAuthorId());
//
//    // When
//    commentService.deleteComment(user.getId(), comment.getId());
//
//    // Then
//    assertThat(commentService.getCommentById(user.getId(), comment.getId()).getIsDeleted()).isTrue();
//  }
//
//    // TODO: 관리자가 댓글 제거하는 경우?
//    // TODO: 이건 @PreAuthorize 도입할 때 같이하기
//
//  // 댓글 조회 파트
//  // ============================================================
//  @Test
//  @DisplayName("유효하지 않은 댓글을 조회할 때 null을 출력함")
//  void getCommentWithWrongCommentId() {
//    // When
//    assertThatThrownBy(() -> commentService.getCommentById(21231L))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("대상 댓글을 갖는 게시글이 유효하지 않을 때 에러 발생")
//  void getCommentWithInvalidPost() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//    CommunityPost post = CommunityPost.builder()
//            .boardId(boardId)
//            .authorId(user.getId())
//            .title("댓글 테스트용 포스트")
//            .content("댓글 테스트용 포스트 입니다.")
//            .build();
//    Long postNum = postService.addPost(user.getId(), post);
//
//    CommunityComment comment = CommunityComment.builder()
//            .postId(postNum)
//            .authorId(user.getId())
//            .content("테스트용 댓글이에요")
//            .build();
//
//    Long commentNum = commentService.addComment(user.getId(), comment);
//    postService.deletePost(user.getId(), postNum);
//
//    // When
//    assertThatThrownBy(() -> commentService.getCommentById(commentNum))
//            .isInstanceOf(IllegalArgumentException.class);
//  }
//
//  @Test
//  @DisplayName("유효한 댓글을 조회할 때 대상 댓글을 출력함")
//  void getCommentOnRightCondition() {
//    // When
//    CommunityComment comment = commentService.getCommentById(commentId);
//
//    // Then
//    assertThat(comment).isNotNull();
//  }
//
//  @Test
//  @DisplayName("관리자가 댓글 조회 할 경우 유효하지 않은 게시글의 댓글도 조회함")
//  void getCommentWithManager() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//    CommunityPost post = CommunityPost.builder()
//            .boardId(boardId)
//            .authorId(user.getId())
//            .title("댓글 테스트용 포스트")
//            .content("댓글 테스트용 포스트 입니다.")
//            .build();
//    Long postNum = postService.addPost(user.getId(), post);
//    CommunityComment comment = CommunityComment.builder()
//            .postId(postNum)
//            .authorId(user.getId())
//            .content("테스트용 댓글이에요")
//            .build();
//    Long commentNum = commentService.addComment(user.getId(), comment);
//    postService.deletePost(user.getId(), postNum);
//
//    CommunityUser manager = userService.getUserById(managerId);
//
//    // When
//    CommunityComment tmp = commentService.getCommentById(manager.getId(), commentNum);
//
//    // Then
//    assertThat(tmp).isNotNull();
//  }
//    // 여러 개 조회
//      //
//  @Test
//  @DisplayName("원하는 Post의 모든 Comment 가져오게 하기")
//  void getCommentsOnSpecificPost() {
//    // Given
//    CommunityPost post = postService.getPostById(postId);
//
//    // When
//    List<CommunityComment> comments = commentService.getCommentsByPostId(post.getId());
//
//    // Then
//    assertThat(comments).allMatch(comment -> comment.getPostId().equals(post.getId()));
//  }
//      //
//  @Test
//  @DisplayName("원하는 Author의 모든 Comment 가져오게 하기")
//  void getCommentsOnSpecificAuthor() {
//    // Given
//    CommunityUser user = userService.getUserById(userId);
//
//    // When
//    List<CommunityComment> comments = commentService.getCommentsByAuthorId(user.getId());
//
//    // Then
//    assertThat(comments).allMatch(comment -> comment.getAuthorId().equals(user.getId()));
//  }
//}
