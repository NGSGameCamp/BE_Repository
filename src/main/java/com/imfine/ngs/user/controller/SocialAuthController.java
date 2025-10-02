package com.imfine.ngs.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialAuthController {

    @GetMapping("/login/{provider}")
    public ResponseEntity<Void> redirectToProvider(@PathVariable String provider) {
        // Use a relative redirect to the backend's OAuth2 authorization endpoint
        String location = "/oauth2/authorization/" + provider;
        return ResponseEntity.status(302).header("Location", location).build();
    }
}
