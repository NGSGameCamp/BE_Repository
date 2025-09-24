package com.imfine.ngs.user.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.user.dto.request.ProfileRequest;
import com.imfine.ngs.user.dto.response.ProfileResponse;
import com.imfine.ngs.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/u")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(profileService.getProfileById(principal.getUserId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal JwtUserPrincipal principal,
                                              @RequestBody ProfileRequest request) {
        profileService.updateProfileById(principal.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal JwtUserPrincipal principal) {
        profileService.deleteUserById(principal.getUserId());
        return ResponseEntity.noContent().build();
    }

}
