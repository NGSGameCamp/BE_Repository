package com.imfine.ngs.community.controller.mapper;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityBoardCreateRequest;
import com.imfine.ngs.community.dto.response.CommunityPostResponse;
import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.service.CommunityTagService;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityMapper {
  private static final String DEFAULT_ROLE = "USER";

  private final CommunityTagService tagService;
  private final UserRepository userRepository;

  public CommunityBoard toCommunityBoard(
          CommunityBoardCreateRequest request,
          CommunityUser user,
          @AuthenticationPrincipal JwtUserPrincipal principal) {
    return CommunityBoard.builder()
            .gameId(request.getGameId())
            .title(request.getTitle())
            .description(request.getDescription())
            .managerId(user != null ? user.getId() : getCommunityUserOrAnonymous(principal).getId())
            .build();
  }

  public CommunityPostResponse toCommunityPostResponse(CommunityPost post) {
    CommunityUser author = getAuthorOrThrow(post.getAuthorId());
    return CommunityPostResponse.from(post, author);
  }

  private CommunityUser loadUser(Long userId, HttpStatus status, String message) {
    return userRepository.findById(userId)
            .map(CommunityUser::getInstance)
            .orElseThrow(() -> new ResponseStatusException(status, message));
  }

  public CommunityUser getAuthorOrThrow(Long authorId) {
    return loadUser(authorId, HttpStatus.NOT_FOUND, "작성자를 찾을 수 없습니다.");
  }

  public CommunityUser getCommunityUserOrAnonymous(JwtUserPrincipal principal) {
    if (principal == null) {
      return CommunityUser.builder().role(DEFAULT_ROLE).build();
    }

    return loadUser(principal.getUserId(), HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
  }

  public CommunityUser getCommunityUserOrThrow(JwtUserPrincipal principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
    }
    return getCommunityUserOrAnonymous(principal);
  }

  public List<CommunityTag> toTagsForMutation(List<String> tagNames) {
    List<String> normalizedNames = normalizeTagNames(tagNames);
    if (normalizedNames.isEmpty()) {
      return Collections.emptyList();
    }

    return normalizedNames.stream()
            .map(tagService::getTagByName)
            .collect(Collectors.toList());
  }

  public List<CommunityTag> toTagsForSearch(List<String> tagNames) {
    List<String> normalizedNames = normalizeTagNames(tagNames);
    if (normalizedNames.isEmpty()) {
      return Collections.emptyList();
    }

    return normalizedNames.stream()
            .map(name -> CommunityTag.builder().name(name).build())
            .collect(Collectors.toList());
  }

  private List<String> normalizeTagNames(List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return Collections.emptyList();
    }

    return tagNames.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .collect(Collectors.toList());
  }
}
