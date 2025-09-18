package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.CommunityBoardRepository;
import com.imfine.ngs.community.repository.CommunityTagRepository;
import com.imfine.ngs.community.repository.TestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityBoardService {
  CommunityBoardRepository boardRepo;
  TestUserRepository userRepo;

  @Autowired
  CommunityBoardService(
          CommunityBoardRepository boardRepo,
          TestUserRepository userRepo) {
    this.boardRepo = boardRepo;
    this.userRepo = userRepo;
  }

  // Create
  public void addBoard(CommunityBoard board) {
    boardRepo.save(board);
  }

  // Read
  public Long count() { return boardRepo.count(); }

  public CommunityBoard getBoardById(Long managerId, Long id) {
//    TestUser tmpUser = null;
//    if (managerId != null)
//      tmpUser = userRepo.findById(managerId).orElse(null);

    CommunityBoard board = boardRepo.findById(id).isPresent() ? boardRepo.findById(id).get() : null;
//    if (board == null)
//      return null;
//    System.out.println("Board: " + board.getId());
//    if (!board.getIsDeleted())
//      return board;
//    if (tmpUser == null)
//      return null;

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

    board.updateIsDeleted(isDeleted);
    boardRepo.save(board);
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
    if (!board.getManagerId().equals(fromUserId))
      return;
    if (tmpUser != null && !tmpUser.getRole().equals("MANAGER"))
      return;

    board.updateManagerId(toUserId);
    boardRepo.save(board);
  }
}
