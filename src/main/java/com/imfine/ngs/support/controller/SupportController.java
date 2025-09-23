package com.imfine.ngs.support.controller;

import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportCategoryRepository;
import com.imfine.ngs.support.service.SupportCategoryService;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportCategoryRepository supportCategoryRepository;

    // 추후 세션에서 가져올 예정. 임시로 대체
    private Long getCurrentUserId() {
        return 1L;
    }

    @PostMapping("{category}")
    public ResponseEntity<Support> createSupportGame(@PathVariable String category, @RequestBody SupportRequestDto support) {
        long userId = getCurrentUserId();

        SupportCategory supportCategory = supportCategoryRepository.findByNameIgnoreCase(category)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("잘못된 카테고리 : " + category)));

        Support result = supportService.insertSupportRepo(userId, support, supportCategory);
        return ResponseEntity.ok(result);
    }


}
