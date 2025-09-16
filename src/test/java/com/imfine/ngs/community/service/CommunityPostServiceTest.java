package com.imfine.ngs.community.service;

import com.imfine.ngs.community.CommunityPost;
import com.imfine.ngs.community.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityPostServiceTest {
  CommunityPostService postService;
  TestUserService userService;
  CommunityBoardService boardService;

  @Autowired
  CommunityPostServiceTest(
          CommunityPostService postService,
          TestUserService userService,
          CommunityBoardService boardService) {
    this.postService = postService;
    this.userService = userService;
    this.boardService = boardService;
  }

  @BeforeEach
  void setUp() {
    // 보드 생성
    // 유저 생성
    for (int i = 0; i < 5; ++i) {
      User user = User.builder()
              .name("postTest"+i)
              .build();
    }
    // 포스트 생성
    for (int i = 0; i < 10; ++i) {
      CommunityPost post = CommunityPost.builder()
              .boardId(1)
              .title("this is test post for post test" + i)
              .content("this is test post for post test" + i +"\n yes.")
              .build();
    }
  }


  // 게시글 작성 파트
  // ============================================================
  @Test
  @DisplayName("글 작성 시 내용이 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨.")
  void writePostWithNoContent() {
    // Given: 준비
    User user = userService.getUserById(2);
    CommunityPost contentlessPost = CommunityPost.builder()
            .boardId(1)
            .title("내용이 없을 경우")
            .content("")
            .build();
    CommunityPost titlelessPost = CommunityPost.builder()
            .boardId(1)
            .title("")
            .content("제목이 없을 경우")
            .build();
    int postCnt = postService.count();

    // When: 실행
    postService.addPost(user, contentlessPost);
    postService.addPost(user, titlelessPost);

    // Then: 검증
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("글 작성 시 유저가 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨")
  void writePostWithWrongUserId() {
    // Given
    User user = User.builder()
            .name("WrongUser")
            .build();
    CommunityPost post = CommunityPost.builder()
            .boardId(1)
            .title("유저가 유효하지 않을 경우")
            .content("ㅇㅇ")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("게시판이 존재하지 않을 경우 글을 작성해도 개수가 늘어나면 안 됨")
  void writePostOnWrongBoard() {
    // Given
    User user = userService.getUserById(2);
    CommunityPost post = CommunityPost.builder()
            .boardId(500)
            .title("Wrong Board")
            .content("Post on Wrong Board")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt+1);
  }

  @Test
  @DisplayName("올바르게 글 작성 시 글의 개수가 1개 늘어나야 함")
  void writePostOnRightConditions() {
    // Given
    User user = userService.getUserById(2);
    CommunityPost post = CommunityPost.builder()
            .boardId(1)
            .title("올바른 제목")
            .content("올바른 내용")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt+1);
  }

    // TODO: 태그가 유효한지 체크???????????????????????????????????
      // 근데 태그는 없으면 추가하게 하고싶은데..
      // 아예 서비스로 빼놔야 하나

  // 게시글 삭제 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 삭제 시도할 경우 글의 개수가 줄어들면 안 됨")
  void deletePostWithWrongUserId() {
    // Given
    CommunityPost post = postService.getPostById(2);
    User user = userService.getUserById(
            post.getAuthorId() > 1 ? post.getAuthorId()-1 : post.getAuthorId()+1);
    int postCnt = postService.count();

    // When
    postService.deletePost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("올바른 유저가 게시글을 삭제할 경우 글의 개수가 줄어들어야 함")
  void deletePostOnRightConditions() {
    // Given
    CommunityPost post = postService.getPostById(2);
    User user = userService.getUserById(post.getAuthorId());
    int postCnt = postService.count();

    // When
    postService.deletePost(user, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt-1);
  }

    // TODO: 관리자가 게시글을 지울 수 있는지 확인?
  void deletePostWithManagerRole() {
    User user = User.builder()
            .name("Manager")
            .role("MANAGER")
            .build();
    CommunityPost post = postService.getPostById(2);


    // When
    postService.deletePostById(user, post.getId());

    // Then
    assertThat(postService.getPostById(post.getId())).isNull();
  }

  // 게시글 수정 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 수정 시도할 경우 글이 수정되면 안 됨")
  void editPostWithWrongUserId() {
    // Given
    CommunityPost fromPost = postService.getPostById(2);
    User user = User.builder()
            .id(500)
            .name("WrongUser")
            .build();
    CommunityPost toPost = CommunityPost.builder()
            .title("새로운 제목")
            .content("새로운 내용")
            .build();
    // When
    postService.editPost(user, fromPost, toPost);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(fromPost);
  }

  @Test
  @DisplayName("올바르지 않은 내용으로 수정 시도할 경우 수정되면 안 됨")
  void editPostWithNoContent() {
    // Given
    CommunityPost fromPost = postService.getPostById(2);
    User user = userService.getUserById(fromPost.getAuthorId());
    CommunityPost toPost1 = CommunityPost.builder()
            .title("NoContent")
            .content("")
            .build();
    CommunityPost toPost2 = CommunityPost.builder()
            .title("")
            .content("NoTitle")
            .build();

    // When
    postService.editPost(user, fromPost, toPost1);
    postService.editPost(user, fromPost, toPost2);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(fromPost);
  }

  @Test
  @DisplayName("올바른 조건으로 수정 시도할 경우 그대로 수정이 되어야 함")
  void editPostOnRightCondition() {
    // Given
    CommunityPost fromPost = postService.getPostById(2);
    User user = userService.getUserById(fromPost.getAuthorId());
    CommunityPost toPost = CommunityPost.builder()
            .id(fromPost.getId())
            .boardId(fromPost.getBoardId())
            .authorId(fromPost.getAuthorId())
            .title("새로운 제목입니다.")
            .content("새로운 내용입니다.")
            .build();

    // When
    postService.editPost(user, fromPost, toPost);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(toPost);
  }

  // 게시글 조회 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 게시글을 조회 시도할 경우 null이 출력되어야 함")
  void getPostWithWrongPostId() {
    // When
    CommunityPost target = postService.getPostById(1023);

    // Then
    assertThat(target).isNull();
  }


    // TODO: 게시글의 게시판이 유효하지 않으면 null이 출력되어야 함
  void getPostWithWrongBoardId() {
    // Given


    // When


    // Then
  }


  @Test
  @DisplayName("올바른 게시글을 조회할 경우 원하는 게시판을 출력해야 함")
  void getPostOnRightCondition() {
    // Given
    CommunityPost target = postService.getPostById(2);

    // When
    CommunityPost result = postService.getPostById(2);

    // Then
    assertThat(result).isEqualTo(target);
  }
    // TODO: 여러개의 개시글을 조회하는 케이스는 뭘 어떻게 검사해야 할까?
      // 여러개 미리 올려두고
}
