package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupportService {

    private final SupportRepo supportRepo;

    public Support insertSupportRepo(Support support) {
        return supportRepo.save(support);
    }

    public List<Support> findSupportByUserId(long userId) {
        return supportRepo.findAllByUserId(userId);
    }

    public List<Support> findSupportByCategoryId(long categoryId) {
        return supportRepo.findAllByCategoryId(categoryId);
    }

    public List<Support> findAllSupport() {
        return supportRepo.findAll();
    }

    public Support findById(long id) {
        return supportRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        id + ": 존재하지 않는 문의글입니다."
                ));
    }

    // 정렬하는 메서드
    public List<Support> sortByDate(String sortBy, List<Support> supports) {

        Comparator<Support> comparator = switch (sortBy.toUpperCase()) {
            case "DESC" -> Comparator.comparing(Support::getCreatedAt).reversed();
            case "ASC" -> Comparator.comparing(Support::getCreatedAt);
            default -> throw new IllegalArgumentException("Invalid sort criteria: " + sortBy +
                    ". Allowed values are: DESC, ASC");
        };

        return supports.stream().sorted(comparator).collect(Collectors.toList());
    }
}
