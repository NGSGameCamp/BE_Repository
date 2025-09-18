package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityBoard;
import com.imfine.ngs.community.dto.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityBoardService {

  public void addBoard(CommunityBoard board) { }

  public CommunityBoard getBoardById(int i) { return null;}
  public CommunityBoard getBoardById(int managerId, int id) { return null; }

  public void setActive(int managerId, int boardId, boolean b) { }

  public void deletePost(int managerId, int boardId, boolean b) { }

  public int count() { return 0; }

  public void setDescription(int userId, int boardId, String desc) { }

  public void setManager(int fromUserId, int toUserId) { }

  public List<CommunityBoard> getAllBoards() { return null;}

  public List<CommunityBoard> getBoardsByKeyword(String keyword) { return null; }
}
