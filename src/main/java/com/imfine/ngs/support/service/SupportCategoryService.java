package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportCategoryService {

    private final SupportCategoryRepository supportCategoryRepository;

    public SupportCategory findByName(String name) {
        return supportCategoryRepository.findByName(name);
    }

}
