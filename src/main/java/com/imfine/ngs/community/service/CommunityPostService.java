package com.imfine.ngs.community.service;

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

  Long addPost(Long userId, CommunityPost post) {
    post.setAuthorId(userId);

    if (post.getContent() == null || post.getContent().isBlank())
      return null;
    if (post.getTitle() == null || post.getTitle().isBlank())
      return null;

    return postRepo.save(post).getId();
  }

  public Long count() { return postRepo.count(); }

  public CommunityPost getPostById(Long postId) {
    return postRepo.findById(postId).orElse(null);
  }

  public Long editPost(Long userId, Long fromPostId, CommunityPost toPost) {
    CommunityPost post = postRepo.findById(fromPostId).orElse(null);
    TestUser user = userRepo.findById(userId).orElse(null);

    if (post == null || user == null) {
      return fromPostId;
    }

    if (!validate.isValidUser(post.getAuthorId(), user))
      return post.getId();

    post.updateContent(toPost.getContent());
    post.updateTitle(toPost.getTitle());
    post.updateTags(toPost.getTags());

    return postRepo.save(post).getId();
  }

  public void deletePost(Long userId, Long postId) {
    TestUser user = userRepo.findById(userId).orElse(null);
    CommunityPost post = getPostById(postId);

    if (user != null && user.getRole().equals("MANAGER"))
      return;

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
