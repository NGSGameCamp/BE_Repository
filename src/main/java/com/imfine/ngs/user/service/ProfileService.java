package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.request.ProfileRequest;
import com.imfine.ngs.user.dto.response.ProfileResponse;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updateProfile(String email, ProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (request.getNickname() != null) {
            user.updateNickname(request.getNickname());
        }

        if (request.getProfileUrl() != null) {
            user.updateProfileUrl(request.getProfileUrl());
        }

        if (request.getPwd() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPwd()));
        }

        userRepository.save(user);
    }

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        return ProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profile_url(user.getProfileUrl())
                .birth_at(user.getBirthAt())
                .build();
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        userRepository.delete(user);
    }

    // id 기반 메서드 (JWT principal과 사용)
    public ProfileResponse getProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return ProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profile_url(user.getProfileUrl())
                .birth_at(user.getBirthAt())
                .build();
    }

    public void updateProfileById(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (request.getNickname() != null) {
            user.updateNickname(request.getNickname());
        }
        if (request.getProfileUrl() != null) {
            user.updateProfileUrl(request.getProfileUrl());
        }
        if (request.getPwd() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPwd()));
        }
        userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));
        userRepository.delete(user);
    }

}
