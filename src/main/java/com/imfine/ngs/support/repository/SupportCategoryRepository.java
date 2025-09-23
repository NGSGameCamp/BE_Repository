package com.imfine.ngs.support.repository;

import com.imfine.ngs.support.entity.SupportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportCategoryRepository extends JpaRepository<SupportCategory, Integer> {
    Optional<SupportCategory> findByName(String name);
    Optional<SupportCategory> findByNameIgnoreCase(String name);
}
