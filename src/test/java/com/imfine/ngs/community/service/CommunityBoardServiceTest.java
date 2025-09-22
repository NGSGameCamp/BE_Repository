package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.TestUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class CommunityBoardServiceTest {
  CommunityBoardService boardService;
  TestUserService userService;

  @Autowired
  CommunityBoardServiceTest(
          CommunityBoardService communityBoardService,
          TestUserService testUserService) {
    this.boardService = communityBoardService;
    this.userService = testUserService;
  }

  Long managerId;
  Long boardId;

  @BeforeEach
  public void setup() {
    // 매니저 생성
    TestUser manager = TestUser.builder()
            .name("Manager")
            .role("MANAGER")
            .build();
    managerId = userService.addUser(manager);

    // 유저 생성
    for (int i = 0; i < 10; ++i) {
      TestUser user = TestUser.builder()
              .name("User" + i)
              .role("USER")
              .build();

      userService.addUser(user);
    }

    // 보드 생성
    for (int i = 0; i < 10; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("board" + i)
              .gameId((long) i)
              .managerId(managerId)
              .description("this is test board for board test" + i)
              .build();

      Long tmp = boardService.addBoard(board);
      if (i == 2)
        boardId = tmp;
    }
  }

  // 게시판 생성 파트
  // ============================================================
  @Test
  @DisplayName("제목 없이 게시판을 등록 시도할 경우 에러")
  void addBoardWithNoTitle() {
    // Given
    CommunityBoard board = CommunityBoard.builder().build();
    Long boardCnt = boardService.count();

    // When & Then
    assertThatThrownBy(() -> boardService.addBoard(board))
            .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("올바른 방법으로 게시판을 등록할 경우 게시판의 개수가 늘어나야 함")
  void addBoardOnRightCondition() {
    // Given
    CommunityBoard board = CommunityBoard.builder()
            .title("RightBoard")
            .gameId(100L)
            .managerId(1L)
            .build();
    Long boardCnt = boardService.count();

    // When
    boardService.addBoard(board);

    // Then
    assertThat(boardService.count()).isEqualTo(boardCnt + 1);
  }


  // 게시판 변경 파트
  // ============================================================
  @Test
  @DisplayName("관리자가 아닌 사람이 isDeleted를 변경시키는 경우 에러")
  void changeIsActiveWithWrongUser() {
    // Given
    CommunityBoard board = boardService.getBoardById(boardId);
    TestUser user = TestUser.builder()
            .id(15123L)
            .name("asdf")
            .role("USER")
            .build();

    // When
    assertThatThrownBy(() -> boardService.setIsDeleted(user.getId(), board.getId(), true))
            .isInstanceOf(IllegalArgumentException.class);
  }
    //
  @Test
  @DisplayName("담당자나 관리자가 아닌 사람이 description을 변경시키는 경우 에러")
  void changeDescriptionWithWrongUser() {
    // Given
    CommunityBoard board = boardService.getBoardById(boardId);
    TestUser user = userService.getUserById(board.getManagerId()+1);
    String desc = "바뀔 내용입니다.";

    // When
    assertThatThrownBy(() -> boardService.setDescription(user.getId(), board.getId(), desc))
            .isInstanceOf(IllegalArgumentException.class);
  }
    //
  @Test
  @DisplayName("관리자가 isDeleted를 변경시키는 경우 변경되어야 함")
  void changeIsActiveWithManager() {
    // Given
    CommunityBoard board = CommunityBoard.builder()
            .title("test")
            .managerId(managerId)
            .gameId(101L)
            .description("teasdfasd")
            .build();
    boardService.addBoard(board);

    TestUser user = userService.getUserById(board.getManagerId());

    // When
    assertThatThrownBy(() -> boardService.setIsDeleted(user.getId(), board.getId(), false))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("담당자가 description을 변경시키는 경우 변경되어야 함")
  void changeDescriptionWithManager() {

    // Given
    CommunityBoard board = boardService.getBoardById(boardId);
    TestUser user = userService.getUserById(board.getManagerId());
    String desc = "바뀔 내용입니다.";

    // When
    boardService.setDescription(user.getId(), board.getId(), desc);

    // Then
    assertThat(boardService.getBoardById(board.getId()).getDescription()).isEqualTo(desc);
  }
  // 담당자 변경
    //
  @Test
  @DisplayName("담당자가 아닌 사람이 관리자를 변경할 경우엔 변경되면 안 됨")
  void changeManagerWithNotManager() {
    CommunityBoard board = boardService.getBoardById(boardId);
    TestUser notFromManager = userService.getUserById(managerId+1);
    TestUser notToManager = userService.getUserById(managerId+2);

    // When
    assertThatThrownBy(() -> boardService.setManager(board.getId(), notFromManager.getId(), notToManager.getId()))
            .isInstanceOf(IllegalArgumentException.class);;
  }

    // 관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함
  @Test
  @DisplayName("관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함")
  void changeManagerOnRightCondition() {
    CommunityBoard board = boardService.getBoardById(2L);
    TestUser manager = userService.getUserById(board.getManagerId());
    Long testUserId = 5L;

    // When
    boardService.setManager(board.getId(), manager.getId(), testUserId);

    // Then
    assertThat(boardService.getBoardById(board.getId()).getManagerId()).isEqualTo(testUserId);
  }

  // 게시판 조회 파트
  // ============================================================
  @Test
  @DisplayName("보드가 존재하지 않으면 조회되면 안 됨")
  void getBoardWithInvalidBoardId() {
    // When
    assertThatThrownBy(() -> boardService.getBoardById(12321L))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("보드가 비활성화 됐으면 조회하면 안 됨")
  void getBoardWithNoActivation() {
    // Given
    CommunityBoard board = CommunityBoard.allBuilder()
            .title("isDeletedTest")
            .description("the board for testing isDeleted")
            .managerId(1L)
            .gameId(101L)
            .isDeleted(true)
            .build();

    Long tmpId = boardService.addBoard(board);

    // When
    assertThatThrownBy(() -> boardService.getBoardById(tmpId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("보드가 유효하면 조회되어야 함")
  void getBoardOnRightCondition() {
    // When
    CommunityBoard board = boardService.getBoardById(boardId);

    // Then
    assertThat(board).isNotNull();
  }

  @Test
  @DisplayName("관리자가 조회할 경우 보드가 비활성화 되어있어도 조회할 수 있어야 함")
  void getDisabledBoardWithManager() {
    // Given
    CommunityBoard board = CommunityBoard.allBuilder()
            .title("isDeletedTest")
            .description("the board for testing isDeleted")
            .managerId(managerId)
            .gameId(101L)
            .isDeleted(false)
            .build();
    boardService.addBoard(board);
    TestUser user = userService.getUserByName("Manager");

    // When
    CommunityBoard result = boardService.getBoardById(user.getId(), board.getId());

    // Then
    assertThat(result).isEqualTo(board);
  }

  @Test
  @DisplayName("보드를 전체조회 할 경우 모두 조회되어야 함")
  void getAllBoards() {
    // Given
    Long boardCnt = boardService.count();

    // When
    List<CommunityBoard> boards = boardService.getAllBoards();

    // Then
    assertThat(boards).hasSize(boardCnt.intValue());
  }

  @Test
  @DisplayName("모든 보드는 검색어를 포함해야 함")
  void getAllBoardsWithKeyword() {
    // Given
    String keyword = "test";

    // When
    List<CommunityBoard> boards = boardService.getBoardsByKeyword(keyword);

    // Then
    assertThat(boards).allMatch(board -> board.getTitle().contains(keyword));
  }
}
