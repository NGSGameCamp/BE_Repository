package com.imfine.ngs.user.controller;

import com.imfine.ngs._global.config.security.CustomUserDetails;
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

    @GetMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(profileService.getProfile(userDetails.getUsername()));
    }

    @GetMapping("/details")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String email) {
        return ResponseEntity.ok(profileService.getProfile(email));
    }

    @PutMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody ProfileRequest request) {
        profileService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        profileService.deleteUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
