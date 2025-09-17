package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.repository.FollowRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Test
    @DisplayName("팔로우 성공")
    void followSucess() {
        Follow follow = followService.follow(1L, TargetType.USER, 2L);

        assertEquals(1L, follow.getUserId());
        assertEquals(TargetType.USER, follow.getTargetType());
        assertEquals(2L, follow.getTargetId());
    }

    @Test
    @DisplayName("팔로우 중복")
    void followDuplicate() {
        Follow follow = followService.follow(1L, TargetType.USER, 2L);

        assertThrows(IllegalArgumentException.class, () -> followService.follow(1L, TargetType.USER, 2L));
    }


    @Test
    @DisplayName("언팔로우 성공")
    void unfollowSucess() {
        followService.follow(1L, TargetType.USER, 2L);

        followService.unfollow(1L, TargetType.USER, 1L);

        assertFalse(followRepository.existsByUserIdAndTargetTypeAndTargetId(1L, TargetType.USER, 2L));
    }

    @Test
    @DisplayName("언팔로우 실패 - 존재하지 않음")
    void unfollowfail() {
        assertThrows(IllegalArgumentException.class, () -> followService.unfollow(1L, TargetType.USER, 2L));
    }

}
