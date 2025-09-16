package com.imfine.ngs.community.service;

import com.imfine.ngs.community.CommunityBoard;
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
public class CommunityBoardServiceTest {
  CommunityBoardService boardService;

  @Autowired
  CommunityBoardServiceTest(CommunityBoardService communityBoardService) {
    this.boardService = communityBoardService;
  }

  @BeforeEach
  public void setup() {
    for (int i = 0; i < 5; ++i) {
      CommunityBoard board = CommunityBoard.builder()
              .title("board" + i)
              .description("this is test board for board test" + i)
              .build();

      boardService.addBoard(board);
    }
  }

  // TODO: 보드는 어떤 서비스가 필요하지
  // 게시판 생성 파트
  // ============================================================

  // TODO: 게시판 생성할 때.. 뭘 해야하지

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
}
