package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportCategoryService {

    private final SupportCategoryRepository supportCategoryRepository;

    public SupportCategory findByName(String categoryName) {
        return supportCategoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 : " + categoryName));
    }

}
