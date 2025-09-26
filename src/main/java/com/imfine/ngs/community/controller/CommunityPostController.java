package com.imfine.ngs.community.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.dto.CommunityPostSearchForm;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityPostCreateRequest;
import com.imfine.ngs.community.dto.request.CommunityPostUpdateRequest;
import com.imfine.ngs.community.dto.response.CommunityPostResponse;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.service.CommunityPostService;
import com.imfine.ngs.community.service.CommunityTagService;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityPostController {

  private final CommunityPostService communityPostService;
  private final CommunityTagService communityTagService;
  private final UserRepository userRepository;

  @PostMapping("/boards/{boardId}/posts")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CommunityPostResponse> createPost(
      @PathVariable Long boardId,
      @RequestBody @Valid CommunityPostCreateRequest request,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = requireCommunityUser(principal);
    List<CommunityTag> tags = resolveTagsForMutation(request.getTags());

    CommunityPost newPost = CommunityPost.builder()
        .boardId(boardId)
        .authorId(communityUser.getId())
        .title(request.getTitle())
        .content(request.getContent())
        .tags(tags)
        .build();

    try {
      Long createdId = communityPostService.addPost(communityUser, newPost);
      CommunityPost created = communityPostService.getPostById(communityUser, createdId);
      return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<CommunityPostResponse> getPost(
      @PathVariable Long postId,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = resolveCommunityUser(principal);
    try {
      CommunityPost post = communityPostService.getPostById(communityUser, postId);
      return ResponseEntity.ok(toResponse(post));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }
  }

  @PutMapping("/posts/{postId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CommunityPostResponse> updatePost(
      @PathVariable Long postId,
      @RequestBody @Valid CommunityPostUpdateRequest request,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = requireCommunityUser(principal);
    List<CommunityTag> tags = resolveTagsForMutation(request.getTags());

    CommunityPost targetPost = CommunityPost.builder()
        .boardId(null)
        .authorId(communityUser.getId())
        .title(request.getTitle())
        .content(request.getContent())
        .tags(tags)
        .build();

    try {
      Long updatedId = communityPostService.editPost(communityUser, postId, targetPost);
      CommunityPost updated = communityPostService.getPostById(communityUser, updatedId);
      return ResponseEntity.ok(toResponse(updated));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  @DeleteMapping("/posts/{postId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deletePost(
      @PathVariable Long postId,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = requireCommunityUser(principal);
    try {
      communityPostService.deletePost(communityUser, postId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  @GetMapping("/boards/{boardId}/posts")
  public ResponseEntity<Page<CommunityPostResponse>> getPosts(
      @PathVariable Long boardId,
      @AuthenticationPrincipal JwtUserPrincipal principal,
      @RequestParam(value = "type", required = false) SearchType searchType,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "tags", required = false) List<String> tagNames,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size
  ) {
    CommunityUser communityUser = resolveCommunityUser(principal);
    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "created_at");
    List<CommunityTag> tags = resolveTagsForSearch(tagNames);

    CommunityPostSearchForm searchForm = CommunityPostSearchForm.builder()
        .type(searchType != null ? searchType : SearchType.TITLE_ONLY)
        .keyword(keyword != null ? keyword : "")
        .tagList(tags)
        .pageable(pageable)
        .build();

    try {
      Page<CommunityPost> posts = communityPostService.getPostsWithSearch(communityUser, boardId, searchForm);
      Page<CommunityPostResponse> response = posts.map(this::toResponse);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  private CommunityPostResponse toResponse(CommunityPost post) {
    CommunityUser author = resolveAuthor(post.getAuthorId());
    return CommunityPostResponse.from(post, author);
  }

  private CommunityUser resolveCommunityUser(JwtUserPrincipal principal) {
    if (principal == null) {
      return CommunityUser.builder().role("USER").build();
    }

    return userRepository.findById(principal.getUserId())
        .map(CommunityUser::getInstance)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
  }

  private CommunityUser resolveAuthor(Long authorId) {
    return userRepository.findById(authorId)
        .map(CommunityUser::getInstance)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "작성자를 찾을 수 없습니다."));
  }

  private CommunityUser requireCommunityUser(JwtUserPrincipal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
    }
    return resolveCommunityUser(principal);
  }

  private List<CommunityTag> resolveTagsForMutation(List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return Collections.emptyList();
    }

    return tagNames.stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .map(communityTagService::getTagByName)
        .collect(Collectors.toList());
  }

  private List<CommunityTag> resolveTagsForSearch(List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return Collections.emptyList();
    }

    return tagNames.stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .map(name -> CommunityTag.builder().name(name).build())
        .collect(Collectors.toList());
  }
}
