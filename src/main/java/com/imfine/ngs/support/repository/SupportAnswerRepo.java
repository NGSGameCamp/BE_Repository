package com.imfine.ngs.support.repository;

import com.imfine.ngs.support.entity.SupportAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportAnswerRepo extends JpaRepository<SupportAnswer, Integer> {
    SupportAnswer findBySupportId(long supportId);
    Optional<SupportAnswer> findById(long id);
}
