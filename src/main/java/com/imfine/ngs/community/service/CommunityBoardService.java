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
    // 매니저인지 조회하기 위한 tmpUser
    // TODO: PreAuthorize로 변경 필요
    TestUser tmpUser = null;
    if (managerId != null)
      tmpUser = userRepo.findById(managerId).orElse(null);

    CommunityBoard board = boardRepo.findById(id).orElse(null);
    if (board == null)
      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");
    if (!board.getIsDeleted())
      return board;
    if (tmpUser == null) // 매니저가 아님
      throw new IllegalArgumentException("비활성화된 게시판입니다!");

    return board; // 비활성화된 게시판인데, 조회 주체가 매니저임
  }
  public CommunityBoard getBoardById(Long id) {
    return getBoardById(null, id);
  }

  /*
   * TODO: 특정 권한이라면 진짜 다 보여주고, 아니면 isDeleted = true 인건 보여주지 않기
   *  테스트 추가 작성 필요?
   */
  public List<CommunityBoard> getAllBoards() {
    return boardRepo.findAll();
  }

  /* TODO: 특정 권한이라면 다 검색해주고, 아니면 isDeleted = true 인건 보여주지 않기
   *  테스트 추가 작성 필요?
   */
  public List<CommunityBoard> getBoardsByKeyword(String keyword) {
    return boardRepo.findCommunityBoardsByTitleContains(keyword);
  }

  // Update
  /* TODO: 마지막 if문에 특정 권한을 가질 경우 검사 추가
   *  테스트 추가 작성 필요?
   */
  public void setIsDeleted(Long managerId, Long boardId, boolean isDeleted) {
    CommunityBoard board = getBoardById(boardId);

    if (board == null)
      throw new IllegalArgumentException("불가능한 접근입니다!");

    if (managerId.equals(board.getManagerId())) {
      board.updateIsDeleted(isDeleted);
      boardRepo.save(board);
    }

    throw new IllegalArgumentException("불가능한 접근입니다!");
  }

  // Update
  /* TODO: 마지막 if문에 특정 권한을 가질 경우 검사 추가
   *  테스트 추가 작성 필요?
   */
  public void setDescription(Long userId, Long boardId, String desc) {
    CommunityBoard board = getBoardById(boardId);
    if (board == null) throw new IllegalArgumentException("불가능한 접근입니다!");

    if (!board.getManagerId().equals(userId))
      throw new IllegalArgumentException("불가능한 접근입니다!");

    board.updateDescription(desc);
    boardRepo.save(board);
  }

  // TODO: 권한검사 좀 섹시하게 하기
  public void setManager(Long boardId, Long fromUserId, Long toUserId) {
    CommunityBoard board = getBoardById(boardId);
    TestUser tmpUser = userRepo.findById(fromUserId).isPresent() ? userRepo.findById(fromUserId).get() : null;
    if (board == null)
      throw new IllegalArgumentException("불가능한 접근입니다!");
    if (!board.getManagerId().equals(fromUserId) && (tmpUser != null && !tmpUser.getRole().equals("MANAGER")))
      throw new IllegalArgumentException("불가능한 접근입니다!");

    board.updateManagerId(toUserId);
    boardRepo.save(board);
  }
}
