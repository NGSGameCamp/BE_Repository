package com.imfine.ngs.community.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.controller.mapper.CommunityMapper;
import com.imfine.ngs.community.dto.CommunityUser;
import com.imfine.ngs.community.dto.request.CommunityBoardCreateRequest;
import com.imfine.ngs.community.dto.request.CommunityBoardDescriptionDto;
import com.imfine.ngs.community.dto.request.CommunityBoardManagerDto;
import com.imfine.ngs.community.dto.response.CommunityBoardCreateResponse;
import com.imfine.ngs.community.entity.CommunityBoard;
import com.imfine.ngs.community.service.CommunityBoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityBoardController {
  private final CommunityBoardService boardService;
  private final CommunityMapper mapper;

  /**
   * 게시판을 생성합니다.
   * @param principal
   * @param boardReq
   * @return
   */
  @PostMapping("/boards/create")
  public ResponseEntity<CommunityBoardCreateResponse> createBoard(
          @AuthenticationPrincipal JwtUserPrincipal principal,
          CommunityBoardCreateRequest boardReq
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);
    CommunityBoard board = mapper.toCommunityBoard(boardReq, user, principal);
    try {
      Long boardId = boardService.addBoard(board);
      return ResponseEntity.ok(CommunityBoardCreateResponse.builder()
              .boardId(boardId)
              .build());
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
  }

  /**
   * 게시판의 설명을 변경합니다.
   * @param boardId
   * @param principal
   * @param descriptionDto
   * @return
   */
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/boards/title/{boardId}")
  public ResponseEntity<Void> updateBoardDescription(
          @PathVariable Long boardId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestBody @Valid CommunityBoardDescriptionDto descriptionDto
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);

    try {
      boardService.setDescription(user, boardId, descriptionDto.getDescription());
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 게시판의 담당자를 변경합니다.
   * @param boardId
   * @param principal
   * @param managerDto
   * @return
   */
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/boards/manager/{boardId}")
  public ResponseEntity<Void> updateBoardManager(
          @PathVariable Long boardId,
          @AuthenticationPrincipal JwtUserPrincipal principal,
          @RequestBody @Valid CommunityBoardManagerDto managerDto
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);
    CommunityUser target = mapper.getAuthorOrThrow(managerDto.getManagerId());

    try {
      boardService.setManager(boardId, user, target);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }

  /**
   * 게시판을 제거합니다.
   * @param boardId
   * @param principal
   * @return
   */
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/boards/delete/{boardId}")
  public ResponseEntity<Void> deleteBoard(
          @PathVariable Long boardId,
          @AuthenticationPrincipal JwtUserPrincipal principal
  ) {
    CommunityUser user = mapper.getCommunityUserOrThrow(principal);

    try {
      boardService.deleteBoard(user, boardId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }
  }
}
