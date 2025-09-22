package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.repository.CommunityBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityBoardService {
  private final CommunityBoardRepository boardRepository;
  // Create
  public Long addBoard(CommunityBoard board) {
    return boardRepository.save(board).getId();
  }

  // Read
  public int count() { return (int) boardRepository.count(); }

  /*
   * TODO: JWT 열어서 권한 체크 하도록 변경
   *  그러고 분기점 만들기??
   *   일단 @PreAuthorize는 최대한 생각하지 않고 만들어보자.. 다 고려하려니까 끝이 없다
   */
  /*
   * 1. JWT를 깐다
   *  -> 내가 안 까는게 좋은 것 같음
   *  -> 저쪽에서 까서 DTO에 담아주라고 해야겠음
   * 2. role을 확인한다
   * 3-1. USER 일 경우
   *  board가 null이 아니고, getManagerId가 본인이랑 같을 때 반환하기
   *    -> USER일 경우는 글이 비활성화 되면 작성자도 못 봄
   * 3-2. MANAGER 일 경우
   *  board가 null이 아닐 경우에만 반환하기
   * 4. 나머지는 (권한 없음) 예외 던지기
   */
  public CommunityBoard getBoardById(CommunityUser user, Long boardId) {
    String role = user.getRole();
    return switch(role) {
      case "USER" -> {
        CommunityBoard board = boardRepository.findById(boardId).orElse(null);
        if (board != null && !board.getIsDeleted())
          yield board;
        else throw new IllegalArgumentException("해당하는 게시판 없음 에러");
      }
      case "MANAGER" -> {
        CommunityBoard board = boardRepository.findById(boardId).orElse(null);
        if (board != null)
          yield board;
        else throw new IllegalArgumentException("해당하는 게시판 없음 에러");
      }
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }
  /*
   * 이런 식으로 유저를 특정하지 않는 검색이 필요한지 모르겠음........
   *  일단 빼고 생각해보자..
   *    -> 필요하네..
   */
  public CommunityBoard getBoardById(Long boardId) {
    // 가라 유저
    CommunityUser tmpUser = CommunityUser.builder()
            .role("USER")
            .build();
    return getBoardById(tmpUser, boardId);
  }

  /*
   * TODO: 특정 권한이라면 진짜 다 보여주고, 아니면 isDeleted = true 인건 보여주지 않기
   *  테스트 추가 작성 필요?
   */
  public List<CommunityBoard> getAllBoards(CommunityUser user) {
    return switch (user.getRole()) {
      case "USER" -> boardRepository.findCommunityBoardsByIsDeletedIsFalse();
      case "MANAGER" -> boardRepository.findAll();
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }
//  public List<CommunityBoard> getAllBoards() {
//
//    return getAllBoards(null);
//  }

  /* TODO: 특정 권한이라면 다 검색해주고, 아니면 isDeleted = true 인건 보여주지 않기
   *  테스트 추가 작성 필요?
   */
  public List<CommunityBoard> getBoardsByKeyword(CommunityUser user, String keyword) {
    return switch (user.getRole()) {
      case "USER" -> boardRepository.findCommunityBoardsByIsDeletedAndTitleContains(false, keyword);
      case "MANAGER" -> boardRepository.findCommunityBoardsByTitleContains(keyword);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  // Update
  public void deleteBoard(CommunityUser user, Long boardId) {
    CommunityBoard board = boardRepository.findById(boardId).orElse(null);
    if (board == null)
      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    switch (user.getRole()) {
      case "USER" -> {
        if (board.getIsDeleted() || !board.getManagerId().equals(user.getId()))
          throw new IllegalArgumentException("유효하지 않은 접근입니다!");
      }
      case "MANAGER" -> {
        if (board.getIsDeleted())
          throw new IllegalArgumentException("이미 비활성화된 게시판입니다!");
      }
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    }

    board.updateIsDeleted(true);
    boardRepository.save(board);
  }

  // Update
  /*
   * TODO: 마지막 if문에 특정 권한을 가질 경우 검사 추가
   *  테스트 추가 작성 필요?
   */
  public void setDescription(CommunityUser user, Long boardId, String description) {
    CommunityBoard board = getBoardById(user, boardId);
    if (board == null) throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    switch (user.getRole()) {
      case "USER" -> {
        if (!board.getManagerId().equals(user.getId()))
          throw new IllegalArgumentException("수정 권한이 없습니다!");
      }
      case "MANAGER" -> {}
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    }
    board.updateDescription(description);
    boardRepository.save(board);
  }

//  public void setManager(Long boardId, Long fromUserId, Long toUserId) {
//    CommunityBoard board = getBoardById(boardId);
//    CommunityUser tmpUser = userRepo.findById(fromUserId).isPresent() ? userRepo.findById(fromUserId).get() : null;
//    if (board == null)
//      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");
//    if (!board.getManagerId().equals(fromUserId) && (tmpUser != null && !tmpUser.getRole().equals("MANAGER")))
//      throw new IllegalArgumentException("접근 권한이 없습니다!");
//
//    board.updateManagerId(toUserId);
//    boardRepository.save(board);
//  }
  public void setManager(Long boardId, CommunityUser user, CommunityUser newManager) {
    CommunityBoard board = getBoardById(user, boardId);
    if (board == null) throw new IllegalArgumentException("유효하지 않은 보드입니다!");

    switch (user.getRole()) {
      case "USER" -> {
        if (!board.getManagerId().equals(user.getId()))
          throw new IllegalArgumentException("수정 권한이 없습니다!");
      }
      case "MANAGER" -> {}
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    }
    board.updateManagerId(newManager.getId());
    boardRepository.save(board);
  }
}
