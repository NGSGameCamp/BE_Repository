package com.imfine.ngs.support.controller;

import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportCategoryService supportCategoryService;

    // 추후 세션에서 가져올 예정. 임시로 대체
    private Long getCurrentUserId() {
        return 1L;
    }

    @PostMapping("{category}")
    public ResponseEntity<Support> createSupportGame(@PathVariable String category, @RequestBody SupportRequestDto support) {
        long userId = getCurrentUserId();

        SupportCategory supportCategory = supportCategoryService.findByNameIgnoreCase(category);

        Support result = supportService.insertSupportRepo(userId, support, supportCategory);

        return ResponseEntity.ok(result);
    }


}
