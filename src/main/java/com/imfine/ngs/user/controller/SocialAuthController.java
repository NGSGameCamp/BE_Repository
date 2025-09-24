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
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/")
                .path(provider)
                .build()
                .toUriString();
        return ResponseEntity.status(302).header("Location", url).build();
    }
}
