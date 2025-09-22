package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.CommunityBoardRepository;
import com.imfine.ngs.community.repository.CommunityTagRepository;
import com.imfine.ngs.community.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityBoardService {
  private final CommunityBoardRepository boardRepo;
  private final TestUserRepository userRepo;

  // Create
  public Long addBoard(CommunityBoard board) {
    return boardRepo.save(board).getId();
  }

  // Read
  public Long count() { return boardRepo.count(); }

  public CommunityBoard getBoardById(Long managerId, Long id) {
    TestUser tmpUser = null;
    if (managerId != null)
      tmpUser = userRepo.findById(managerId).orElse(null);

    CommunityBoard board = boardRepo.findById(id).orElse(null);
    if (board == null)
      return null;
    if (!board.getIsDeleted())
      return board;
    if (tmpUser == null)
      return null;

    return board;
  }
  public CommunityBoard getBoardById(Long id) {
    return getBoardById(null, id);
  }

  public List<CommunityBoard> getAllBoards() {
    return boardRepo.findAll();
  }

  public List<CommunityBoard> getBoardsByKeyword(String keyword) {
    return boardRepo.findCommunityBoardsByTitleContains(keyword);
  }

  // Update
  public void setIsDeleted(Long managerId, Long boardId, boolean isDeleted) {
    CommunityBoard board = getBoardById(boardId);

    if (board == null)
      throw new IllegalArgumentException("불가능한 접근입니다!");

    if (managerId.equals(board.getManagerId())) {
      board.updateIsDeleted(isDeleted);
      boardRepo.save(board);
    }
  }

  public void setDescription(Long userId, Long boardId, String desc) {
    CommunityBoard board = getBoardById(boardId);
    if (board == null)
      return;
    if (!board.getManagerId().equals(userId))
      return;

    board.updateDescription(desc);
    boardRepo.save(board);
  }

  public void setManager(Long boardId, Long fromUserId, Long toUserId) {
    CommunityBoard board = getBoardById(boardId);
    TestUser tmpUser = userRepo.findById(fromUserId).isPresent() ? userRepo.findById(fromUserId).get() : null;
    if (board == null)
      return;
    if (!board.getManagerId().equals(fromUserId) && (tmpUser != null && !tmpUser.getRole().equals("MANAGER")))
      return;

    board.updateManagerId(toUserId);
    boardRepo.save(board);
  }
}
