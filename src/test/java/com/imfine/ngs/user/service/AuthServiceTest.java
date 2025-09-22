package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.request.SignInRequest;
import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.dto.response.SignInResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원가입 성공")
    void SignUpSuccess() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("a@b.com");
        request.setName("Hun");
        request.setPwd("1234");
        request.setPwdCheck("1234");

        authService.signUp(request);

        User savedUser = userRepository.findByEmail("a@b.com").orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("Hun");
        assertThat(passwordEncoder.matches("1234", savedUser.getPwd())).isTrue();
    }

    @Test
    @DisplayName("로그인 성공")
    void SignInSuccess() {
        User user = User.builder()
                .email("a@b.com")
                .nickname("GameHun")
                .name("Hun")
                .pwd(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        SignInRequest request = new SignInRequest();
        request.setEmail("a@b.com");
        request.setPwd("password");

        SignInResponse response = authService.signIn(request);

        assertThat(response.getAccessToken()).isNotNull();
    }
    /*
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
     */
}

