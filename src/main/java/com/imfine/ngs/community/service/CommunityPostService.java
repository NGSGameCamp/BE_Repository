package com.imfine.ngs.community.service;

import com.imfine.ngs.community.dto.CommunityPostSearchForm;
import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    // TODO
    Pageable pageable = PageRequest.of(0, 1);
    return (long) postRepository.findCommunityPostsByBoardId(boardId, pageable).getContent().size();
  }

  public CommunityPost getPostById(CommunityUser user, Long postId) {
    CommunityPost post = postRepository.findById(postId).orElse(null);
    if (post == null) throw new IllegalArgumentException("유효하지 않은 게시물입니다!");

    CommunityBoard board = boardService.getBoardById(user, post.getBoardId());
    return switch (user.getRole()) {
      case "USER" -> {
        if (board.getIsDeleted() || post.getIsDeleted())
          throw new IllegalArgumentException("존재하지 않는 게시글입니다!");

        yield post;
      }
      case "MANAGER" -> post;
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }
  public CommunityPost getPostById(Long postId) {
    CommunityUser tmpUser = CommunityUser.builder()
            .nickname("tmpUser")
            .build();
    return getPostById(tmpUser, postId);
  }

  public Long editPost(CommunityUser user, Long fromPostId, CommunityPost toPost) {
    CommunityPost fromPost = postRepository.findById(fromPostId).orElse(null);
    if (fromPost == null) throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    if (!fromPost.getAuthorId().equals(user.getId()))
      throw new IllegalArgumentException("잘못된 접근입니다!");

    if ((toPost.getContent() == null || toPost.getContent().isBlank())
            || (toPost.getTitle() == null || toPost.getTitle().isBlank()))
      throw new IllegalArgumentException("제목이나 내용이 작성되지 않았습니다!");

    fromPost.updateTitle(toPost.getTitle());
    fromPost.updateContent(toPost.getContent());
    fromPost.updateTags(toPost.getTags());

    return postRepository.save(fromPost).getId();
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
  public Page<CommunityPost> getPostsWithSearch(CommunityUser user, Long boardId, CommunityPostSearchForm searchForm) {
    CommunityBoard board = boardService.getBoardById(user, boardId);
    SearchType type = searchForm.type;
    String keyword = searchForm.keyword;
    List<String> tags = new ArrayList<>();
    if (searchForm.tagList != null && !searchForm.tagList.isEmpty())
      for (CommunityTag tag : searchForm.tagList)
        tags.add(tag.getName());
    Pageable pageable = searchForm.pageable;

    return switch (user.getRole()) {
      case "USER" -> {
        if (board.getManagerId().equals(user.getId()))
          yield postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), true, pageable);

        yield postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), false, pageable);
      }
      case "MANAGER" -> postRepository.searchKeywordsWithTags(boardId, type.name(), keyword, tags, tags.size(), true, pageable);
      default -> throw new IllegalArgumentException("권한 잘못됨 에러");
    };
  }
}
