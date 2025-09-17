package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityBoard;
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

  @BeforeEach
  public void setup() {
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
              .title("board" + i)
              .description("this is test board for board test" + i)
              .build();

      boardService.addBoard(board);
    }
  }

  // 게시판 생성 파트
  // ============================================================
  @Test
  @DisplayName("제목 없이 게시판을 등록 시도할 경우, 게시판의 개수가 늘어나면 안 됨")
  void addBoardWithNoTitle() {
    // Given
    CommunityBoard board = CommunityBoard.builder().build();
    int boardCnt = boardService.count();

    // When
    boardService.addBoard(board);

    // Then
    assertThat(boardService.count()).isEqualTo(boardCnt);
  }

  @Test
  @DisplayName("올바른 방법으로 게시판을 등록할 경우 게시판의 개수가 늘어나야 함")
  void addBoardOnRightCondition() {
    // Given
    CommunityBoard board = CommunityBoard.builder()
            .title("RightBoard")
            .build();
    int boardCnt = boardService.count();

    // When
    boardService.addBoard(board);

    // Then
    assertThat(boardService.count()).isEqualTo(boardCnt + 1);
  }


  // 게시판 변경 파트
  // ============================================================
  @Test
  @DisplayName("관리자가 아닌 사람이 isActive를 변경시키는 경우 변경되면 안됨")
  void changeIsActiveWithWrongUser() {
    // Given
    CommunityBoard board = boardService.getBoardById(2);
    User user = userService.getUserById(
            board.getManager_id() > 1 ? board.getManager_id() - 1 : board.getManager_id()+1);

    // When
    boardService.setActive(user.getId(), board.getId(), false);

    // Then
    assertThat(boardService.getBoardById(board.getId()).isActive()).isTrue();
  }
    //
  @Test
  @DisplayName("담당자나 관리자가 아닌 사람이 description을 변경시키는 경우 변경되면 안 됨")
  void changeDescriptionWithWrongUser() {
    // Given
    CommunityBoard board = boardService.getBoardById(2);
    User user = userService.getUserById(
            board.getManager_id() > 1 ? board.getManager_id() - 1 : board.getManager_id()+1);
    String desc = "바뀔 내용입니다.";

    // When
    boardService.setDescription(user.getId(), board.getId(), desc);

    // Then
    assertThat(boardService.getBoardById(board.getId()).getDescription()).isNotEqualTo(desc);
  }
    //
  @Test
  @DisplayName("관리자가 isActive를 변경시키는 경우 변경되어야 함")
  void changeIsActiveWithManager() {
    // Given
    CommunityBoard board = boardService.getBoardById(2);
    User user = userService.getUserById(board.getManager_id());

    // When
    boardService.setActive(user.getId(), board.getId(), false);

    // Then
    assertThat(boardService.getBoardById(board.getId()).isActive()).isFalse();
  }

  @Test
  @DisplayName("담당자가 description을 변경시키는 경우 변경되어야 함")
  void changeDescriptionWithManager() {

    // Given
    CommunityBoard board = boardService.getBoardById(2);
    User user = userService.getUserById(board.getManager_id());
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
    CommunityBoard board = boardService.getBoardById(2);
    User manager = userService.getUserById(
            board.getManager_id() > 1 ? board.getManager_id() - 1 : board.getManager_id()+1);
    User notManager = userService.getUserById(0);

    // When
    boardService.setManager(manager.getId(), notManager.getId());

    // Then
    assertThat(boardService.getBoardById(board.getId()).getManager_id()).isNotEqualTo(notManager.getId());
  }

    //
  @Test
  @DisplayName("담당자를 유효하지 않은 유저로 변경할 경우엔 변경되면 안 됨")
  void changeManagerWithWrongUser() {
    CommunityBoard board = boardService.getBoardById(2);
    User manager = userService.getUserById(
            board.getManager_id() > 1 ? board.getManager_id() - 1 : board.getManager_id()+1);
    User wrongUser = User.builder()
            .id(121321321)
            .name("Wrong User")
            .build();

    // When
    boardService.setManager(manager.getId(), wrongUser.getId());

    // Then
    assertThat(boardService.getBoardById(board.getId()).getManager_id()).isNotEqualTo(wrongUser.getId());
  }

    // 관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함
  @Test
  @DisplayName("관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함")
  void changeManagerOnRightCondition() {
    CommunityBoard board = boardService.getBoardById(2);
    User manager = userService.getUserById(board.getManager_id());
    User user = userService.getUserById(5);

    // When
    boardService.setManager(manager.getId(), user.getId());

    // Then
    assertThat(boardService.getBoardById(board.getId()).getManager_id()).isEqualTo(user.getId());
  }

  // 게시판 조회 파트
  // ============================================================
  @Test
  @DisplayName("보드가 존재하지 않으면 조회되면 안 됨")
  void getBoardWithInvalidBoardId() {
    // When
    CommunityBoard target = boardService.getBoardById(12321);

    // Then
    assertThat(target).isNull();
  }

  @Test
  @DisplayName("보드가 비활성화 됐으면 조회하면 안 됨")
  void getBoardWithNoActivation() {
    // Given
    CommunityBoard board = CommunityBoard.builder()
            .id(50)
            .title("isActiveTest")
            .description("the board for testing isActive")
            .isActive(false)
            .build();
    boardService.addBoard(board);

    // When
    CommunityBoard result = boardService.getBoardById(board.getId());

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("보드가 유효하면 조회되어야 함")
  void getBoardOnRightCondition() {
    // When
    CommunityBoard board = boardService.getBoardById(2);

    // Then
    assertThat(board).isNotNull();
  }

  @Test
  @DisplayName("관리자가 조회할 경우 보드가 비활성화 되어있어도 조회할 수 있어야 함")
  void getDisabledBoardWithManager() {
    // Given
    CommunityBoard board = CommunityBoard.builder()
            .id(50)
            .title("isActiveTest")
            .description("the board for testing isActive")
            .isActive(false)
            .build();
    boardService.addBoard(board);
    User user = userService.getUserByName("Manager");

    // When
    CommunityBoard result = boardService.getBoardById(user.getId(), board.getId());

    // Then
    assertThat(result).isEqualTo(board);
  }

  @Test
  @DisplayName("보드를 전체조회 할 경우 모두 조회되어야 함")
  void getAllBoards() {
    // Given
    int boardCnt = boardService.count();

    // When
    List<CommunityBoard> boards = boardService.getAllBoards();

    // Then
    assertThat(boards).hasSize(boardCnt);
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
