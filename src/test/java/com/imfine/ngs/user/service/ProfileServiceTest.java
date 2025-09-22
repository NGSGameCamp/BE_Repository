package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.response.UserProfileDto;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProfileServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OauthClient oauthClient() {
            return (provider, token) -> new OauthUserInfo("dummy@test.com", "Dummy");
        }
    }
    /*
    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNicknameSuccess() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        profileService.updateNickname("a@b.com", "NewHun");
        User user = userRepository.findByEmail("a@b.com").orElseThrow();
        assertEquals("NewHun", user.getNickname());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void userDetailSuccess() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        UserProfileDto profile = profileService.getUserProfile("a@b.com");
        assertEquals("a@b.com", profile.getEmail());
        assertEquals("Hun", profile.getNickname());
    }

    @Test
    @DisplayName("프로필 조회 실패")
    void userDetailFail() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class,
                () -> profileService.getUserProfile("c@d.com"));
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteUserSuccess() {
        authService.signUp("a@b.com", "1234","1234", "Hun");
        profileService.deleteUser("a@b.com");
        assertFalse(userRepository.findByEmail("a@b.com").isPresent());
    }

    @Test
    @DisplayName("회원 삭제 실패 - 존재하지 않는 이메일")
    void deleteUserFail() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class,
                () -> profileService.deleteUser("c@d.com"));
    }

     */
}

