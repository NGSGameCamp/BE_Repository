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
}

