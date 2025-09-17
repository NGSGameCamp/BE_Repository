package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportCategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportCategoryService {

    private final SupportCategoryRepo supportCategoryRepo;

    public SupportCategory findByName(String name) {
        return supportCategoryRepo.findByName(name);
    }

}
