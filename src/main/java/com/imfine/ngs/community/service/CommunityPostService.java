package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
  private final CommunityPostRepository postRepository;
  private final CommunityBoardService boardService;

  Long addPost(CommunityUser user, CommunityPost post) {
    CommunityBoard board = boardService.getBoardById(user, post.getBoardId());

    if (board == null) throw new IllegalArgumentException("유효하지 않은 보드입니다!");
    if (post.getContent() == null || post.getContent().isBlank())
      throw new IllegalArgumentException("내용이 비었습니다!");
    if (post.getTitle() == null || post.getTitle().isBlank())
      throw new IllegalArgumentException("제목이 비었습니다!");

    return postRepository.save(post).getId();
  }

  public Long count() { return postRepository.count(); }
  public Long count(Long boardId) {
    return (long) postRepository.findCommunityPostsByBoardId(boardId).size();
  }

  public CommunityPost getPostById(CommunityUser user, Long postId) {
    CommunityPost post = postRepository.findById(postId).orElse(null);
    if (post == null) throw new IllegalArgumentException("유효하지 않은 게시물입니다!");

    CommunityBoard board = boardService.getBoardById(user, post.getBoardId());
    return switch (user.getRole()) {
      case "USER" -> {
        if (board.getIsDeleted() || post.getIsDeleted())
          throw new IllegalArgumentException("존재하지 않는 게시글입니다!");
        if (!board.getManagerId().equals(user.getId()))
          throw new IllegalArgumentException("게시글을 볼 권한이 없습니다!");

        yield post;
      }
      case "MANAGER" -> post;
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  public Long editPost(CommunityUser user, CommunityPost post) {
    if (boardService.getBoardById(user, post.getBoardId()) == null) throw new IllegalArgumentException("유효하지 않은 게시판입니다!");
    if ((post.getContent() == null || post.getContent().isBlank())
            || (post.getTitle() == null || post.getTitle().isBlank()))
      throw new IllegalArgumentException("제목이나 내용이 작성되지 않았습니다!");

    if (!post.getAuthorId().equals(user.getId()))
      throw new IllegalArgumentException("잘못된 접근입니다!");

    return postRepository.save(post).getId();
  }

  public void deletePost(CommunityUser user, Long postId) {
    CommunityPost post = getPostById(user, postId);

    switch (user.getRole()) {
      case "USER" -> {
        if (!post.getAuthorId().equals(user.getId()))
          throw new IllegalArgumentException("잘못된 접근입니다!");
      }
      case "MANAGER" -> {}
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    }

    post.updateIsDeleted(true);
    postRepository.save(post);
  }

  public List<CommunityPost> getPostsWithSearch(CommunityUser user, Long boardId, SearchType type, String keyword) {
    CommunityBoard board = boardService.getBoardById(user, boardId);
    return switch (user.getRole()) {
      case "USER" -> {
        if (board.getManagerId().equals(user.getId()))
          yield postRepository.searchKeywords(boardId, type.name(), keyword, true);

        yield postRepository.searchKeywords(boardId, type.name(), keyword, false);
      }
      case "MANAGER" -> postRepository.searchKeywords(boardId, type.name(), keyword, true);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }

  public List<CommunityPost> getPostsWithSearch(CommunityUser user, Long boardId, SearchType type, String keyword, List<CommunityTag> list) {
    List<String> tags = new ArrayList<>();
    for (CommunityTag tag : list) {
      tags.add(tag.getName());
    }

    CommunityBoard board = boardService.getBoardById(user, boardId);
    return switch (user.getRole()) {
      case "USER" -> {
        if (board.getManagerId().equals(user.getId()))
          yield postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), true);

        yield postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), false);
      }
      case "MANAGER" -> postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), true);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }
}
