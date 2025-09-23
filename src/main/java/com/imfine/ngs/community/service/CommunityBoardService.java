package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.repository.CommunityBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  public Page<CommunityBoard> getAllBoards(CommunityUser user) {
    // TODO
    Pageable pageable = PageRequest.of(0, 1);
    return getAllBoards(user, pageable);
  }
  public Page<CommunityBoard> getAllBoards(CommunityUser user, Pageable pageable) {
    return switch (user.getRole()) {
      case "USER" -> boardRepository.findCommunityBoardsByIsDeletedIsFalse(pageable);
      case "MANAGER" -> boardRepository.findAll(pageable);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  public Page<CommunityBoard> getBoardsByKeyword(CommunityUser user, String keyword) {
    // TODO
    Pageable pageable = PageRequest.of(0, 1);
    return getBoardsByKeyword(user, keyword, pageable);
  }
  public Page<CommunityBoard> getBoardsByKeyword(CommunityUser user, String keyword, Pageable pageable) {
    return switch (user.getRole()) {
      case "USER" -> boardRepository.findCommunityBoardsByIsDeletedAndTitleContains(false, keyword, pageable);
      case "MANAGER" -> boardRepository.findCommunityBoardsByTitleContains(keyword, pageable);
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
