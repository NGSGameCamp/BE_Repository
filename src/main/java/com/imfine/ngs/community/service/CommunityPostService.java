package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.repository.CommunityPostRepository;
import com.imfine.ngs.community.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
  private final ValidationService validate;
  private final CommunityPostRepository postRepo;
  private final TestUserRepository userRepo;
  private final CommunityBoardService boardService;

  /* TODO: 잘못된 Board일 때 추가하면 안 됨. 케이스 추가 필요
   *  테스트 추가 필요?
   * TODO: addPost할 때 userId인자 없이 post 완성해서 받기4
   */
  Long addPost(Long userId, CommunityPost post) {
    post.setAuthorId(userId);
    CommunityBoard board = boardService.getBoardById(post.getBoardId());

    if (post.getContent() == null || post.getContent().isBlank())
      throw new IllegalArgumentException("내용이 비었습니다!");
    if (post.getTitle() == null || post.getTitle().isBlank())
      throw new IllegalArgumentException("제목이 비었습니다!");
    if (board == null)
      throw new IllegalArgumentException("유효하지 않은 게시판입니다!");

    return postRepo.save(post).getId();
  }

  public Long count() { return postRepo.count(); }

  /* TODO: isDeleted에 대한 케이스 추가 필요
   *  테스트 추가 필요?
   */
  public CommunityPost getPostById(Long postId) {
    CommunityPost post = postRepo.findById(postId).orElse(null);
    if  (post == null)
      throw new IllegalArgumentException("유효하지 않은 게시글입니다!");
    return post;
  }

  // TODO: 권한체크 섹시하게 할 것
  public Long editPost(Long userId, Long fromPostId, CommunityPost toPost) {
    CommunityPost post = postRepo.findById(fromPostId).orElse(null);
    TestUser user = userRepo.findById(userId).orElse(null);

    if (post == null || user == null) {
      throw new IllegalArgumentException("불가능한 접근입니다!");
    }

    if (!validate.isValidUser(post.getAuthorId(), user))
      throw new IllegalArgumentException("접근 권한이 없습니다!");

    if (toPost.getContent() == null || toPost.getContent().isBlank())
      throw new IllegalArgumentException("내용이 비었습니다!");
    if (toPost.getTitle() == null || toPost.getTitle().isBlank())
      throw new IllegalArgumentException("제목이 없습니다!");

    post.updateContent(toPost.getContent());
    post.updateTitle(toPost.getTitle());
    post.updateTags(toPost.getTags());

    return postRepo.save(post).getId();
  }

  // TODO: 권한 체크 섹시하게 할 것
  public void deletePost(Long userId, Long postId) {
    TestUser user = userRepo.findById(userId).orElse(null);
    CommunityPost post = getPostById(postId);

    if (user != null && (user.getRole().equals("MANAGER") || !post.getAuthorId().equals(userId)))
      throw new IllegalArgumentException("접근 권한이 없습니다!");

    post.updateIsDeleted(true);
    postRepo.save(post);
  }

  public List<CommunityPost> getPostsWithSearch(Long boardId, SearchType type, String keyword) {
    return postRepo.searchKeywords(boardId, type.name(), keyword);
  }

  public List<CommunityPost> getPostsWithSearch(Long boardId, SearchType type, String keyword, List<CommunityTag> list) {
    List<String> tags = new ArrayList<>();
    for (CommunityTag tag : list) {
      tags.add(tag.getName());
    }

    return postRepo.searchKeywordsWithTags(boardId, type.toString(), keyword, tags, tags.size());
  }
}
