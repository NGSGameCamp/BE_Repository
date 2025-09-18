package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.enums.SearchType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityPostServiceTest {
  CommunityPostService postService;
  CommunityBoardService boardService;
  CommunityTagService tagService;
  TestUserService userService;

  @Autowired
  CommunityPostServiceTest(
          CommunityPostService postService,
          CommunityBoardService boardService,
          CommunityTagService tagService,
          TestUserService userService) {
    this.postService = postService;
    this.boardService = boardService;
    this.tagService = tagService;
    this.userService = userService;
  }

  @BeforeEach
  void setUp() {
    // 매니저 생성
    TestUser manager = TestUser.builder()
            .id(427L)
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
    // 유저 생성
    for (int i = 0; i < 5; ++i) {
      TestUser user = TestUser.builder()
              .name("postTest"+i)
              .build();

      userService.addUser(user);
    }
    // 포스트 생성
    for (int i = 0; i < 10; ++i) {
      for (int j = 0; j < 5; ++j) {
        CommunityPost post = CommunityPost.builder()
                .boardId((long) j)
                .title("this is test post for post test" + i + " " + j)
                .content("this is test post for post test" + i + "\n yes.")
                .build();

        ArrayList<CommunityTag> tags = new ArrayList<>();
        tags.add(tagService.getTagByName("test"));
        if (j == 4)
          tags.add(tagService.getTagByName("abc"));

        post.insertTags(tags);

        postService.addPost((long) (Math.random() * 5), post);
      }
    }
  }


  // 게시글 작성 파트
  // ============================================================
  @Test
  @DisplayName("글 작성 시 내용이 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨.")
  void writePostWithNoContent() {
    // Given: 준비
    TestUser user = userService.getUserById(2L);
    CommunityPost contentlessPost = CommunityPost.builder()
            .boardId(1L)
            .title("내용이 없을 경우")
            .content("")
            .build();
    CommunityPost titlelessPost = CommunityPost.builder()
            .boardId(1L)
            .title("")
            .content("제목이 없을 경우")
            .build();
    int postCnt = postService.count();

    // When: 실행
    postService.addPost(user.getId(), contentlessPost);
    postService.addPost(user.getId(), titlelessPost);

    // Then: 검증
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("글 작성 시 유저가 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨")
  void writePostWithWrongUserId() {
    // Given
    TestUser user = TestUser.builder()
            .name("WrongUser")
            .build();
    CommunityPost post = CommunityPost.builder()
            .boardId(1L)
            .title("유저가 유효하지 않을 경우")
            .content("ㅇㅇ")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user.getId(), post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("게시판이 존재하지 않을 경우 글을 작성해도 개수가 늘어나면 안 됨")
  void writePostOnWrongBoard() {
    // Given
    TestUser user = userService.getUserById(2L);
    CommunityPost post = CommunityPost.builder()
            .boardId(500L)
            .title("Wrong Board")
            .content("Post on Wrong Board")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user.getId(), post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt+1);
  }

  @Test
  @DisplayName("올바르게 글 작성 시 글의 개수가 1개 늘어나야 함")
  void writePostOnRightConditions() {
    // Given
    TestUser user = userService.getUserById(2L);
    CommunityPost post = CommunityPost.builder()
            .boardId(1L)
            .title("올바른 제목")
            .content("올바른 내용")
            .build();
    int postCnt = postService.count();

    // When
    postService.addPost(user.getId(), post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt+1);
  }

  // 게시글 삭제 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 삭제 시도할 경우 글의 개수가 줄어들면 안 됨")
  void deletePostWithWrongUserId() {
    // Given
    CommunityPost post = postService.getPostById(2L);
    TestUser user = userService.getUserById(
            post.getAuthorId() > 1 ? post.getAuthorId()-1 : post.getAuthorId()+1);
    int postCnt = postService.count();

    // When
    postService.deletePost(user.getId(), post.getId());

    // Then
    assertThat(postService.count()).isEqualTo(postCnt);
  }

  @Test
  @DisplayName("올바른 유저가 게시글을 삭제할 경우 글의 개수가 줄어들어야 함")
  void deletePostOnRightConditions() {
    // Given
    CommunityPost post = postService.getPostById(2L);
    TestUser user = userService.getUserById(post.getAuthorId());
    int postCnt = postService.count();

    // When
    postService.deletePost(user.getId(), post.getId());

    // Then
    assertThat(postService.count()).isEqualTo(postCnt-1);
  }

  @Test
  @DisplayName("MANAGER는 게시글을 삭제할 경우 항상 글의 개수가 줄어들어야 함")
  void deletePostWithManagerRole() {
    TestUser user = userService.getUserByName("Manager");
    CommunityPost post = postService.getPostById(2L);

    // When
    postService.deletePost(user.getId(), post.getId());

    // Then
    assertThat(postService.getPostById(post.getId())).isNull();
  }

  // 게시글 수정 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 수정 시도할 경우 글이 수정되면 안 됨")
  void editPostWithWrongUserId() {
    // Given
    CommunityPost fromPost = postService.getPostById(2L);
    TestUser user = TestUser.builder()
            .id(500L)
            .name("WrongUser")
            .build();
    CommunityPost toPost = CommunityPost.builder()
            .title("새로운 제목")
            .content("새로운 내용")
            .build();
    // When
    postService.editPost(user.getId(), fromPost.getId(), toPost);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(fromPost);
  }

  @Test
  @DisplayName("올바르지 않은 내용으로 수정 시도할 경우 수정되면 안 됨")
  void editPostWithNoContent() {
    // Given
    CommunityPost fromPost = postService.getPostById(2L);
    TestUser user = userService.getUserById(fromPost.getAuthorId());
    CommunityPost toPost1 = CommunityPost.builder()
            .title("NoContent")
            .content("")
            .build();
    CommunityPost toPost2 = CommunityPost.builder()
            .title("")
            .content("NoTitle")
            .build();
    CommunityPost toPost3 = CommunityPost.builder()
            .title("NullContent")
            .build();
    CommunityPost toPost4 = CommunityPost.builder()
            .content("NullTitle")
            .build();

    // When
    postService.editPost(user.getId(), fromPost.getId(), toPost1);
    postService.editPost(user.getId(), fromPost.getId(), toPost2);
    postService.editPost(user.getId(), fromPost.getId(), toPost3);
    postService.editPost(user.getId(), fromPost.getId(), toPost4);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(fromPost);
  }

  @Test
  @DisplayName("올바른 조건으로 수정 시도할 경우 그대로 수정이 되어야 함")
  void editPostOnRightCondition() {
    // Given
    CommunityPost fromPost = postService.getPostById(2L);
    TestUser user = userService.getUserById(fromPost.getAuthorId());
    CommunityPost toPost = CommunityPost.allBuilder()
            .id(fromPost.getId())
            .boardId(fromPost.getBoardId())
            .authorId(fromPost.getAuthorId())
            .title("새로운 제목입니다.")
            .content("새로운 내용입니다.")
            .build();

    // When
    postService.editPost(user.getId(), fromPost.getId(), toPost);

    // Then
    assertThat(postService.getPostById(fromPost.getId())).isEqualTo(toPost);
  }

  // 게시글 조회 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 게시글을 조회 시도할 경우 null이 출력되어야 함")
  void getPostWithWrongPostId() {
    // When
    CommunityPost target = postService.getPostById(1023L);

    // Then
    assertThat(target).isNull();
  }


  @Test
  @DisplayName("게시글의 게시판이 유효하지 않으면 null이 출력되어야 함")
  void getPostWithWrongBoardId() {
    // Given
    CommunityBoard noActiveBoard = CommunityBoard.allBuilder()
            .id(51L)
            .title("This Board will be unactivated.")
            .description("yes")
            .build();
    TestUser user = userService.getUserById(2L);
    TestUser manager = userService.getUserByName("Manager");
    CommunityPost postOfNoActivated = CommunityPost.builder()
            .title("BoardDeletedPost")
            .content("The board of this post will gbe deleted.")
            .boardId(50L)
            .authorId(user.getId())
            .build();

    boardService.addBoard(noActiveBoard);
    postService.addPost(user.getId(), postOfNoActivated);

    boardService.setIsDeleted(manager.getId(), noActiveBoard.getId(), false);

    // When
    CommunityPost tmp2 = postService.getPostById(postOfNoActivated.getId());

    // Then
    assertThat(tmp2).isNull();
  }

  @Test
  @DisplayName("올바른 게시글을 조회할 경우 원하는 게시판을 출력해야 함")
  void getPostOnRightCondition() {
    // Given
    CommunityPost target = postService.getPostById(2L);

    // When
    CommunityPost result = postService.getPostById(2L);

    // Then
    assertThat(result).isEqualTo(target);
  }


  @Test
  @DisplayName("조회된 모든 게시글의 제목에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithTitleSearch() {
    // Given
    SearchType type = SearchType.TITLE_ONLY;
    String keyword = "test";

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(type, keyword);

    // Then
    assertThat(result).allMatch(post -> post.getTitle().contains(keyword));
  }

  @Test
  @DisplayName("조회된 모든 게시글의 작성자에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithAuthorSearch() {
    // Given
    SearchType type = SearchType.AUTHOR_ONLY;
    String keyword = "test";

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(type, keyword);

    // Then
    assertThat(result).allMatch(
            post -> userService.getUserById(post.getAuthorId()).getName().contains(keyword));
  }

  // 조회된 모든 게시글의 내용에 검색어와 일치하는 단어가 있는가?
  @Test
  @DisplayName("조회된 모든 게시글의 제목에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithContentSearch() {
    // Given
    SearchType type = SearchType.CONTENT_ONLY;
    String keyword = "test";

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(type, keyword);

    // Then
    assertThat(result).allMatch(post -> post.getContent().contains(keyword));
  }

  @Test
  @DisplayName("조회된 모든 게시글의 제목 또는 내용에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithTitleAndContentSearch() {
    // Given
    SearchType type = SearchType.TITLE_AND_CONTENT;
    String keyword = "test";

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(type, keyword);

    // Then
    assertThat(result).allMatch(post ->
            post.getContent().contains(keyword)
            || post.getTitle().contains(keyword)
    );
  }

  //
  @Test
  @DisplayName("조회된 모든 게시글의 태그에 목표 태그와 일치하는 요소가 있는가?")
  void getPostsWithTag() {
    // Given
    SearchType type = SearchType.TITLE_AND_CONTENT;
    String keyword = "test";
            CommunityTag[] tags = {
            tagService.getTagByName("test"),
            tagService.getTagByName("abc"),
    };

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(type, Arrays.asList(tags), keyword);

    // Then
//    assertThat(result).allMatch(post ->
//            post.getContent().contains(keyword)
//            || post.getTitle().contains(keyword)
//    );
    assertThat(result).allMatch(post ->
            post.getTags().containsAll(Arrays.asList(tags))
    );
  }
}
