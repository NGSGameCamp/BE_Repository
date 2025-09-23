package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.dto.CommunityUser;
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

  @Autowired
  CommunityBoardServiceTest(
          CommunityBoardService communityBoardService) {
    this.boardService = communityBoardService;
  }

  Long boardId;
  Long boardId2;
  Long boardId3;
  Long boardId4;
  Long managerId;
  CommunityUser manager;
  CommunityUser boardManager;
  CommunityUser wrongUser;

  @BeforeEach
  public void setup() {
    // 보드 생성
    for (int i = 0; i < 10; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("board" + i)
              .gameId((long) i)
              .managerId((long) (Math.random() * 192831) + 2)
              .description("this is test board for board test" + i)
              .build();

      Long tmp = boardService.addBoard(board);
      if (i == 2) {
        boardId = tmp;
        managerId = board.getManagerId();
        manager = CommunityUser.builder()
                .id(1L)
                .nickname("Manager")
                .role("MANAGER")
                .build();
        boardManager = CommunityUser.builder()
                .id(managerId)
                .nickname("BoardManager")
                .build();
        wrongUser = CommunityUser.builder()
                .id(managerId + 1)
                .nickname("testUser")
                .build();
      }
      if (i == 3) boardId2 = tmp;
      if (i == 4) boardId3 = tmp;
      if (i == 5) boardId4 = tmp;
    }
  }

  // 게시판 생성 파트
  // ============================================================
  @Test
  @DisplayName("제목 없이 게시판을 등록 시도할 경우 에러")
  void addBoardWithNoTitle() {
    // Given
    CommunityBoard board = CommunityBoard.builder().build();
    int boardCnt = boardService.count();

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
            .managerId(managerId)
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
  @DisplayName("담당자나 관리자가 아닌 사람이 isDeleted를 변경시키는 경우 에러")
  void changeIsActiveWithWrongUser() {
    // When & Then
    assertThatThrownBy(() -> boardService.deleteBoard(wrongUser, boardId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("담당자나 관리자가 아닌 사람이 description을 변경시키는 경우 에러")
  void changeDescriptionWithWrongUser() {
    // Given
    String desc = "바뀔 내용입니다.";

    // When & Then
    assertThatThrownBy(() -> boardService.setDescription(wrongUser, boardId, desc))
            .isInstanceOf(IllegalArgumentException.class);
  }
    //
  @Test
  @DisplayName("담당자나 관리자가 아닌 사람이 관리자를 변경시키는 경우 에러")
  void changeManagerWithWrongUser() {
    assertThatThrownBy(() -> boardService.setManager(boardId, wrongUser, manager))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("담당자나 관리자가 isDeleted를 변경시키는 경우 변경되어야 함")
  void changeIsDeletedWithManager() {
    boardService.deleteBoard(manager, boardId3);
    assertThat(boardService.getBoardById(manager, boardId3).getIsDeleted()).isTrue();
  }

  @Test
  @DisplayName("담당자나 관리자가 description을 변경시키는 경우 변경되어야 함")
  void changeDescriptionWithManager() {
    // Given
    String desc = "바뀔 내용입니다.";
    String desc2 = "바뀔 내용입니다.2";

    // When
    boardService.setDescription(boardManager, boardId, desc);
    // Then
    assertThat(boardService.getBoardById(boardManager, boardId).getDescription()).isEqualTo(desc);

    // When
    boardService.setDescription(manager, boardId, desc2);
    // Then
    assertThat(boardService.getBoardById(manager, boardId).getDescription()).isEqualTo(desc2);
  }

    // 관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함
  @Test
  @DisplayName("관리자 또는 담당자가 담당자를 변경할 경우엔 변경되어야 함")
  void changeManagerWithManager() {
    boardService.setManager(boardId, manager, wrongUser);
    assertThat(boardService.getBoardById(wrongUser, boardId).getManagerId()).isEqualTo(wrongUser.getId());
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
    boardService.deleteBoard(boardManager, boardId);
    // When & Then
    assertThatThrownBy(() -> boardService.getBoardById(boardId))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("보드가 유효하면 조회되어야 함")
  void getBoardOnRightCondition() {
    // Then
    assertThat(boardService.getBoardById(boardId)).isNotNull();
  }

  @Test
  @DisplayName("관리자가 조회할 경우 보드가 비활성화 되어있어도 조회할 수 있어야 함")
  void getDisabledBoardWithManager() {
    //Given
    boardService.deleteBoard(manager, boardId4);
    // When && Then
    assertThat(boardService.getBoardById(manager, boardId)).isNotNull();
  }
//
//  @Test
//  @DisplayName("관리자는 보드를 전체 조회 할 경우 모두 조회되어야 함")
//  void getAllBoards() {
//    // When
//    List<CommunityBoard> boards = boardService.getAllBoards(manager);
//
//    // Then
//    assertThat(boards).hasSize(boardService.count());
//  }
//
//  @Test
//  @DisplayName("관리자가 아니고, 비활성화된 보드가 있을 경우 모두 조회 되진 않음")
//  void getAllBoardsWithNoManager() {
//    // Given
//    boardService.deleteBoard(manager, boardId2);
//
//    // When
//    List<CommunityBoard> boards = boardService.getAllBoards(wrongUser);
//
//    // Then
//    assertThat(boards).hasSizeLessThan(boardService.count());
//  }

  @Test
  @DisplayName("모든 보드는 검색어를 포함해야 함")
  void getAllBoardsWithKeyword() {
    // Given
    String keyword = "test";

    // When
    List<CommunityBoard> boards = boardService.getBoardsByKeyword(wrongUser, keyword).getContent();
    List<CommunityBoard> boards2 = boardService.getBoardsByKeyword(manager, keyword).getContent();

    // Then
    assertThat(boards).allMatch(board -> board.getTitle().contains(keyword));
    assertThat(boards2).allMatch(board -> board.getTitle().contains(keyword));
  }
}
