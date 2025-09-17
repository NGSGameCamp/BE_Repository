package com.imfine.ngs.support.repository;

import com.imfine.ngs.support.entity.SupportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportCategoryRepo extends JpaRepository<SupportCategory, Integer> {
    SupportCategory findByName(String name);
}
