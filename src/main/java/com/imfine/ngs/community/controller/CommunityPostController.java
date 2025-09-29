package com.imfine.ngs.community.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.controller.mapper.CommunityMapper;
import com.imfine.ngs.community.dto.CommunityPostSearchForm;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.*;
import com.imfine.ngs.community.dto.response.CommunityPostResponse;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.service.CommunityPostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityPostController {
  private final CommunityMapper mapper;
  private final CommunityPostService postService;

  /**
   * 게시글을 작성합니다.
   * @param boardId
   * @param principal
   * @param request
   * @return
   */
  @PostMapping("/post/{boardId}/create")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CommunityPostResponse> createPost(
          @PathVariable Long boardId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestBody @Valid CommunityPostCreateRequest request
  ) {
    CommunityUser communityUser = mapper.getCommunityUserOrThrow(principal);
    List<CommunityTag> tags = mapper.toTagsForMutation(request.getTags());

    CommunityPost newPost = CommunityPost.builder()
            .boardId(boardId)
            .authorId(communityUser.getId())
            .title(request.getTitle())
            .content(request.getContent())
            .tags(tags)
            .build();

    try {
      Long createdId = postService.addPost(communityUser, newPost);
      CommunityPost created = postService.getPostById(communityUser, createdId);
      return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCommunityPostResponse(created));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 게시글들을 검색어와 태그에 따라 조회합니다.
   * @param boardId
   * @param principal
   * @param searchType
   * @param keyword
   * @param tagNames
   * @param page
   * @param size
   * @return
   */
  @GetMapping("/post/{boardId}/all")
  public ResponseEntity<Page<CommunityPostResponse>> getPosts(
          @PathVariable Long boardId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestParam(value = "type", required = false) SearchType searchType,
          @RequestParam(value = "keyword", required = false) String keyword,
          @RequestParam(value = "tags", required = false) List<String> tagNames,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size
  ) {
    CommunityUser communityUser = mapper.getCommunityUserOrAnonymous(principal);
    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "created_at");
    List<CommunityTag> tags = mapper.toTagsForSearch(tagNames);

    CommunityPostSearchForm searchForm = CommunityPostSearchForm.builder()
            .type(searchType != null ? searchType : SearchType.TITLE_ONLY)
            .keyword(keyword != null ? keyword : "")
            .tagList(tags)
            .pageable(pageable)
            .build();

    try {
      Page<CommunityPost> posts = postService.getPostsWithSearch(communityUser, boardId, searchForm);
      Page<CommunityPostResponse> response = posts.map(mapper::toCommunityPostResponse);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 단일 게시글을 조회합니다.
   * @param postId
   * @param principal
   * @return
   */
  @GetMapping("/post/{postId}")
  public ResponseEntity<CommunityPostResponse> getPost(
      @PathVariable Long postId,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = mapper.getCommunityUserOrAnonymous(principal);
    try {
      CommunityPost post = postService.getPostById(communityUser, postId);
      return ResponseEntity.ok(mapper.toCommunityPostResponse(post));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }
  }

  /**
   * 게시글을 수정합니다.
   * @param postId
   * @param request
   * @param principal
   * @return
   */
  @PutMapping("/post/edit/{postId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> updatePost(
      @PathVariable Long postId,
      @RequestBody @Valid CommunityPostUpdateRequest request,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser communityUser = mapper.getCommunityUserOrThrow(principal);
    List<CommunityTag> tags = mapper.toTagsForMutation(request.getTags());

    CommunityPost targetPost = CommunityPost.builder()
        .boardId(request.getBoardId())
        .authorId(communityUser.getId())
        .title(request.getTitle())
        .content(request.getContent())
        .tags(tags)
        .build();

    try {
      postService.editPost(communityUser, postId, targetPost);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 게시글을 제거합니다.
   * @param postId
   * @param principal
   * @return
   */
  @DeleteMapping("/post/delete/{postId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deletePost(
      @PathVariable Long postId,
      @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);
    try {
      postService.deletePost(user, postId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  // =======================
  // ======== Tags ========
  // =======================

  // TODO: 유사한 태그 조회

  // TODO: 태그 있는지 조회
}
