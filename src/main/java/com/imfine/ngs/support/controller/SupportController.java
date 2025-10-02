package com.imfine.ngs.support.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import com.imfine.ngs.user.dto.response.UserLibraryResponse;
import com.imfine.ngs.user.service.UserLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportCategoryService supportCategoryService;
    private final UserLibraryService userLibraryService;

    @PostMapping("{category}")
    public ResponseEntity<Support> createSupportGame(
            @PathVariable String category, @RequestBody SupportRequestDto support,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        Long userId = principal.getUserId();
        SupportCategory supportCategory = supportCategoryService.findByNameIgnoreCase(category);
        Support result = supportService.insertSupportRepo(userId, support, supportCategory);

        return ResponseEntity.ok(result);
    }

    @GetMapping("{category}")
    public ResponseEntity<List<UserLibraryResponse>> getGameList(@AuthenticationPrincipal JwtUserPrincipal principal, @PathVariable String category) {
        long userId = principal.getUserId();

        return ResponseEntity.ok(userLibraryService.getUserLibrary(userId));
    }
}
