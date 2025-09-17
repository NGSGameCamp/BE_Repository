package com.imfine.ngs.user.service;

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
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OauthClient oauthClient() {
            return (provider, token) -> new OauthUserInfo("dummy@test.com", "Dummy");
        }
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() {
        Long id = authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertNotNull(id);
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void signUpFailPwdDontMatch() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.signUp("a@b.com", "12345", "1234", "Hun"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void signUpFailDuplicateEmail() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class,
                () -> authService.signUp("a@b.com", "1234", "1234", "Hun"));
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = authService.signIn("a@b.com", "1234");
        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signInFailPwdDontMatch() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class,
                () -> authService.signIn("a@b.com", "12345"));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void signInFailEmailDontMatch() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.signIn("hi", "1234"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePwdSuccess() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        User user = authService.signIn("a@b.com", "1234");
        authService.updatePwd(user.getEmail(), "1234", "5678");
        assertEquals("5678", user.getPwd());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 이전 비밀번호 불일치")
    void updatePwdFailPwdDontMatch() {
        authService.signUp("a@b.com", "1234", "1234", "Hun");
        assertThrows(IllegalArgumentException.class,
                () -> authService.updatePwd("a@b.com", "wrongOldPw", "5678"));
    }
}

