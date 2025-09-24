package com.imfine.ngs.support.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportCategoryService supportCategoryService;

    JwtUserPrincipal principal;

    // 추후 세션에서 가져올 예정. 임시로 대체
    private Long getCurrentUserId(Principal principal) {
        if (principal != null) {
            return Long.parseLong(principal.getName());
        } else {
            throw new IllegalArgumentException("principal is null");
        }
    }

    @PostMapping("{category}")
    public ResponseEntity<Support> createSupportGame(@PathVariable String category, @RequestBody SupportRequestDto support, Principal principal) {

        System.out.println(support.getTitle());
        SupportCategory supportCategory = supportCategoryService.findByNameIgnoreCase(category);

        Support result = supportService.insertSupportRepo(principal, support, supportCategory);

        return ResponseEntity.ok(result);
    }


}
