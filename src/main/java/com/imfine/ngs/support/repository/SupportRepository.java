package com.imfine.ngs.support.repository;

import com.imfine.ngs.support.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

    List<Support> findAllByCategoryId(long categoryId);

    List<Support> findAllByUserId(long userId);

    List<Support> findByUserId(long userId);

    Optional<Support> findById(long id);

}
