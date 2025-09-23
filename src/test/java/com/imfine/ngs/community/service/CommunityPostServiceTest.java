package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityBoardSearchForm;
import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.dto.CommunityUser;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityPostServiceTest {
  CommunityPostService postService;
  CommunityBoardService boardService;
  CommunityTagService tagService;

  @Autowired
  CommunityPostServiceTest(
          CommunityPostService postService,
          CommunityBoardService boardService,
          CommunityTagService tagService) {
    this.postService = postService;
    this.boardService = boardService;
    this.tagService = tagService;
  }

  Long managerId;
  Long boardId;
  Long postId;
  CommunityUser manager;
  CommunityUser boardManager;
  CommunityUser correctUser;
  CommunityUser wrongUser;

  @BeforeEach
  void setUp() {
    // 매니저 생성
    manager = CommunityUser.builder()
            .id(1L)
            .nickname("Manager")
            .role("MANAGER")
            .build();
    managerId = manager.getId();

    // 보드 생성
    for (int i = 0; i < 5; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("This is test board for post test" + i)
              .gameId((long) i)
              .managerId(i+10L)
              .build();

      Long tmp = boardService.addBoard(board);
      if (i == 2) {
        boardId = tmp;
        boardManager = CommunityUser.builder()
                .id(i+10L)
                .nickname("board manager")
                .build();
      }
    }

    correctUser = CommunityUser.builder()
            .id((long) (Math.random()*10203212))
            .nickname("Right User")
            .build();
    wrongUser = CommunityUser.builder()
            .id(correctUser.getId()+1)
            .nickname("Wrong User")
            .build();
    // 포스트 생성
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        CommunityPost post = CommunityPost.builder()
                .boardId(boardId)
                .authorId(correctUser.getId())
                .title("this is test post for post test" + i + " " + j)
                .content("this is test post for post test" + i + "\n yes.")
                .build();

        ArrayList<CommunityTag> tags = new ArrayList<>();
        tags.add(tagService.getTagByName("test"));
        if (j == 4)
          tags.add(tagService.getTagByName("abc"));

        post.insertTags(tags);

        Long tmp = postService.addPost(correctUser, post);
        if (i == 2 && j == 2)
          postId = tmp;
      }
    }
  }
  // 게시글 작성 파트
  // ============================================================
  @Test
  @DisplayName("글 작성 시 내용이 유효하지 않으면 작성된 글 개수가 늘어나면 안 됨.")
  void writePostWithNoContent() {
    // Given: 준비
    CommunityPost contentlessPost = CommunityPost.builder()
            .boardId(boardId)
            .authorId(correctUser.getId())
            .title("내용이 없을 경우")
            .content("")
            .build();
    CommunityPost titlelessPost = CommunityPost.builder()
            .boardId(boardId)
            .authorId(wrongUser.getId())
            .title("")
            .content("제목이 없을 경우")
            .build();

    // When & Then
    assertThatThrownBy(() -> postService.addPost(correctUser, contentlessPost))
            .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> postService.addPost(wrongUser, titlelessPost))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("게시판이 존재하지 않을 경우 에러")
  void writePostOnWrongBoard() {
    // Given
    CommunityPost post = CommunityPost.builder()
            .boardId(500L)
            .title("Wrong Board")
            .content("Post on Wrong Board")
            .build();

    // When
    assertThatThrownBy(() -> postService.addPost(correctUser, post))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바르게 글 작성 시 글의 개수가 1개 늘어나야 함")
  void writePostOnRightConditions() {
    // Given
    CommunityPost post = CommunityPost.builder()
            .boardId(boardId)
            .authorId(correctUser.getId())
            .title("올바른 제목")
            .content("올바른 내용")
            .build();
    Long postCnt = postService.count();

    // When
    postService.addPost(correctUser, post);

    // Then
    assertThat(postService.count()).isEqualTo(postCnt+1);
  }

  // 게시글 삭제 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 삭제 시도할 경우 해당 글이 비활성화 되면 안 됨")
  void deletePostWithWrongUserId() {
    // When & Then
    assertThatThrownBy(() -> postService.deletePost(wrongUser, postId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바른 유저가 게시글을 삭제할 경우 글이 비활성화 되어야 함")
  void deletePostOnRightConditions() {
    // When
    postService.deletePost(correctUser, postId);

    // Then
    assertThat(postService.getPostById(manager, postId).getIsDeleted()).isTrue();
  }
  // 게시글 수정 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 유저가 게시글을 수정 시도할 경우 글이 수정되면 안 됨")
  void editPostWithWrongUserId() {
    CommunityPost toPost = CommunityPost.allBuilder()
            .id(postId)
            .boardId(boardId)
            .authorId(wrongUser.getId())
            .title("새로운 제목")
            .content("새로운 내용")
            .build();
    // When
    assertThatThrownBy(() -> postService.editPost(wrongUser, postId, toPost))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바르지 않은 내용으로 수정 시도할 경우 수정되면 안 됨")
  void editPostWithNoContent() {
    // Given
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
    assertThatThrownBy(() -> postService.editPost(correctUser, postId, toPost1))
            .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> postService.editPost(wrongUser, postId, toPost2))
            .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> postService.editPost(wrongUser, postId, toPost3))
            .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> postService.editPost(correctUser, postId, toPost4))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바른 조건으로 수정 시도할 경우 그대로 수정이 되어야 함")
  void editPostOnRightCondition() {
    CommunityPost toPost = CommunityPost.allBuilder()
            .id(postId)
            .boardId(boardId)
            .authorId(correctUser.getId())
            .title("새로운 제목입니다.")
            .content("새로운 내용입니다.")
            .tags(null)
            .build();

    // When
    Long tmp = postService.editPost(correctUser, postId, toPost);

    // Then
    assertThat(postService.getPostById(tmp).getId()).isEqualTo(toPost.getId());
    assertThat(postService.getPostById(tmp).getTitle()).isEqualTo(toPost.getTitle());
    assertThat(postService.getPostById(tmp).getContent()).isEqualTo(toPost.getContent());
    assertThat(postService.getPostById(tmp).getTags()).isEqualTo(toPost.getTags());
  }

  // 게시글 조회 파트
  // ============================================================
  @Test
  @DisplayName("올바르지 않은 게시글을 조회 시도할 경우 null이 출력되어야 함")
  void getPostWithWrongPostId() {
    // When
    assertThatThrownBy(() -> postService.getPostById(correctUser, 1023L))
            .isInstanceOf(IllegalArgumentException.class);
  }


  @Test
  @DisplayName("게시글의 게시판이 유효하지 않으면 에러 발생")
  void getPostWithWrongBoardId() {
    // Given
    CommunityPost postOfNoActivated = CommunityPost.builder()
            .title("BoardDeletedPost")
            .content("The board of this post will gbe deleted.")
            .boardId(boardId)
            .authorId(correctUser.getId())
            .build();

    boardService.deleteBoard(manager, postOfNoActivated.getBoardId());
    assertThatThrownBy(() -> postService.addPost(correctUser, postOfNoActivated))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("올바른 게시글을 조회할 경우 원하는 게시판을 출력해야 함")
  void getPostOnRightCondition() {
    // When & Then
    assertThat(postService.getPostById(postId)).isNotNull();
  }


  @Test
  @DisplayName("조회된 모든 게시글의 제목에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithTitleSearch() {
    // Given
    SearchType type = SearchType.TITLE_ONLY;
    String keyword = "test";
    CommunityBoardSearchForm form = CommunityBoardSearchForm.builder()
            .type(type)
            .keyword(keyword)
            .build();

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(correctUser, boardId, form).getContent();

    // Then
    assertThat(result).allMatch(post -> post.getTitle().contains(keyword));
  }

  /*
   * TODO: 이거 테스트 해볼 방법 연구하기
   *  이거 하려면? 유저를 조회해야함
   *    UserService 사용법 이해하고 써보기
   */
//  @Test
//  @DisplayName("조회된 모든 게시글의 작성자에 검색어와 일치하는 단어가 있는가?")
//  void getPostsWithAuthorSearch() {
//    // Given
//    SearchType type = SearchType.AUTHOR_ONLY;
//    String keyword = "test";
//
//    // When
//    List<CommunityPost> result = postService.getPostsWithSearch(wrongUser, boardId, type, keyword);
//
//    // Then
//    assertThat(result).allMatch(
//            post -> post.getAuthorId().contains(keyword));
//  }

  // 조회된 모든 게시글의 내용에 검색어와 일치하는 단어가 있는가?
  @Test
  @DisplayName("조회된 모든 게시글의 제목에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithContentSearch() {
    // Given
    SearchType type = SearchType.CONTENT_ONLY;
    String keyword = "test";
    CommunityBoardSearchForm form = CommunityBoardSearchForm.builder()
            .type(type)
            .keyword(keyword)
            .build();

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(wrongUser, boardId, form).getContent();

    // Then
    assertThat(result).allMatch(post -> post.getContent().contains(keyword));
  }

  @Test
  @DisplayName("조회된 모든 게시글의 제목 또는 내용에 검색어와 일치하는 단어가 있는가?")
  void getPostsWithTitleAndContentSearch() {
    // Given
    SearchType type = SearchType.TITLE_AND_CONTENT;
    String keyword = "test";
    CommunityBoardSearchForm form = CommunityBoardSearchForm.builder()
            .type(type)
            .keyword(keyword)
            .build();

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(wrongUser, boardId, form).getContent();

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
    CommunityBoardSearchForm form = CommunityBoardSearchForm.builder()
            .type(type)
            .keyword(keyword)
            .tagList(Arrays.asList(tags))
            .build();

    // When
    List<CommunityPost> result = postService.getPostsWithSearch(correctUser, boardId, form).getContent();

    assertThat(result).allMatch(post ->
            post.getTags().containsAll(Arrays.asList(tags))
    );
  }
}
