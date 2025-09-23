package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.FollowRepository;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Follow follow(Long userId, TargetType targetType, Long targetId) {
        if (targetType == TargetType.USER) {
            if (userId != null && userId.equals(targetId)) {
                throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
            }
            if (!userRepository.existsById(targetId)) {
                throw new IllegalArgumentException("대상 사용자가 존재하지 않습니다.");
            }
        }
        if (followRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new IllegalArgumentException("이미 팔로우 한 상태입니다");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

        Follow follow = Follow.builder()
                .user(user)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        return followRepository.save(follow);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void unfollow(Long userId, TargetType targetType, Long targetId) {
        Follow follow = followRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 하지 않은 상태입니다."));

        followRepository.delete(follow);
    }

    public List<Follow> getFollowing(Long userId) {
        return followRepository.findAllByUserId(userId);
    }

    public List<Follow>getFollowers(TargetType targetType, Long targetId) {
        return followRepository.findAllByTargetTypeAndTargetId(targetType, targetId);
    }









}
