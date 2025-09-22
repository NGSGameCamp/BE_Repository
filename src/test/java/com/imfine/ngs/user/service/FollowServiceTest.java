package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.FollowRepository;
import com.imfine.ngs.user.repository.UserRepository;
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
    
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("팔로우 성공")
    void followSucess() {
        User follower = userRepository.save(User.builder()
                .email("a@b.com")
                .pwd("1234")
                .name("Hun")
                .nickname("hunny")
                .build());
        User target = userRepository.save(User.builder()
                .email("c@d.com")
                .pwd("1234")
                .name("Hue")
                .nickname("huey")
                .build());

        Follow follow = followService.follow(follower.getId(), TargetType.USER, target.getId());

        assertEquals(follower.getId(), follow.getUser().getId());
        assertEquals(TargetType.USER, follow.getTargetType());
        assertEquals(target.getId(), follow.getTargetId());
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollowSucess() {
        User follower = userRepository.save(User.builder()
                .email("e@f.com")
                .pwd("1234")
                .name("Hun2")
                .nickname("hunny2")
                .build());
        User target = userRepository.save(User.builder()
                .email("g@h.com")
                .pwd("1234")
                .name("Hue2")
                .nickname("huey2")
                .build());

        followService.follow(follower.getId(), TargetType.USER, target.getId());

        followService.unfollow(follower.getId(), TargetType.USER, target.getId());

        assertFalse(followRepository.existsByUserIdAndTargetTypeAndTargetId(follower.getId(), TargetType.USER, target.getId()));
    }

}
