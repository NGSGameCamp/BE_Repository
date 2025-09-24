package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.entity.UserRole;
import com.imfine.ngs.user.entity.UserStatus;
import com.imfine.ngs.user.repository.FollowRepository;
import com.imfine.ngs.user.repository.UserRepository;
import com.imfine.ngs.user.repository.UserRoleRepository;
import com.imfine.ngs.user.repository.UserStatusRepository;
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

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("팔로우 성공")
    void followSucess() {
        UserRole role = userRoleRepository.findByRole("USER").orElseThrow();
        UserStatus status = userStatusRepository.findByName("ACTIVE").orElseThrow();

        User follower = User.builder()
                .email("a@b.com")
                .pwd("1234")
                .name("Hun")
                .nickname("hunny")
                .build();
        follower.assignRole(role);
        follower.assignStatus(status);
        follower = userRepository.save(follower);

        User target = User.builder()
                .email("c@d.com")
                .pwd("1234")
                .name("Hue")
                .nickname("huey")
                .build();
        target.assignRole(role);
        target.assignStatus(status);
        target = userRepository.save(target);

        Follow follow = followService.follow(follower.getId(), TargetType.USER, target.getId());

        assertEquals(follower.getId(), follow.getUser().getId());
        assertEquals(TargetType.USER, follow.getTargetType());
        assertEquals(target.getId(), follow.getTargetId());
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollowSucess() {
        UserRole role2 = userRoleRepository.findByRole("USER").orElseThrow();
        UserStatus status2 = userStatusRepository.findByName("ACTIVE").orElseThrow();

        User follower = User.builder()
                .email("e@f.com")
                .pwd("1234")
                .name("Hun2")
                .nickname("hunny2")
                .build();
        follower.assignRole(role2);
        follower.assignStatus(status2);
        follower = userRepository.save(follower);

        User target = User.builder()
                .email("g@h.com")
                .pwd("1234")
                .name("Hue2")
                .nickname("huey2")
                .build();
        target.assignRole(role2);
        target.assignStatus(status2);
        target = userRepository.save(target);

        followService.follow(follower.getId(), TargetType.USER, target.getId());

        followService.unfollow(follower.getId(), TargetType.USER, target.getId());

        assertFalse(followRepository.existsByUserIdAndTargetTypeAndTargetId(follower.getId(), TargetType.USER, target.getId()));
    }

}
