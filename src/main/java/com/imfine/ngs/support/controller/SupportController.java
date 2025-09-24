package com.imfine.ngs.support.controller;

import com.google.api.Authentication;
import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportCategoryService supportCategoryService;

    @PostMapping("{category}")
    public ResponseEntity<Support> createSupportGame(
            @PathVariable String category, @RequestBody SupportRequestDto support,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        Long userId = principal.getUserId();
        SupportCategory supportCategory = supportCategoryService.findByNameIgnoreCase(category);
        Support result = supportService.insertSupportRepo(userId, support, supportCategory);

        return ResponseEntity.ok(result);
    }
}
