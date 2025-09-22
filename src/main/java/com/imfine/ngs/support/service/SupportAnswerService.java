package com.imfine.ngs.support.service;

import com.imfine.ngs.support.entity.SupportAnswer;
import com.imfine.ngs.support.repository.SupportAnswerRepository;
import com.imfine.ngs.support.repository.SupportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportAnswerService {

    private final SupportAnswerRepository supportAnswerRepo;
    private final SupportRepository supportRepository;

    public SupportAnswer findBySupportId(long supportId) {
        SupportAnswer answer = supportAnswerRepo.findBySupportId(supportId);
        if (answer == null) {
            throw new EntityNotFoundException("Support id " + supportId + " does not exist");
        }
        return supportAnswerRepo.findBySupportId(supportId);
    }

    public boolean isAnswer(long supportId) {
        if (supportAnswerRepo.findBySupportId(supportId) == null) {
            return false;
        }
        return true;
    }

    public List<SupportAnswer> findAnswerByUserId(long userId) {
        List<SupportAnswer> answers = new ArrayList<>();

        supportRepository.findByUserId(userId).forEach(supportAnswer -> {
            answers.add(findBySupportId(supportAnswer.getId()));
        });

        return answers;
    }
}
