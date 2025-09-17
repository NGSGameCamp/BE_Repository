package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public Follow follow(Long userId, TargetType targetType, Long targetId) {
        if (followRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new IllegalArgumentException("이미 팔로우 한 상태입니다");
        }

        Follow follow = Follow.builder()
                .userId(userId)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        return followRepository.save(follow);
    }

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
