package com.imfine.ngs.user.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.user.dto.response.UserLibraryResponse;
import com.imfine.ngs.user.service.UserLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/u")
public class UserLibraryController {

    private final UserLibraryService userLibraryService;

    @GetMapping("/library")
    @PreAuthorize("isAuthenticated()")
    public List<UserLibraryResponse> getUserLibrary(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal.getUserId();
        return userLibraryService.getUserLibrary(userId);
    }
}
