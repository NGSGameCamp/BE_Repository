package com.imfine.ngs.user.service;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.user.dto.request.SignInRequest;
import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.dto.response.SignInResponse;
import com.imfine.ngs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TokenServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("토큰에서 userId와 role 추출")
    void userId_and_role_from_token() {

        String email = "jaehun1417@gmail.com";
        String pwd = "1234";

        SignUpRequest signUp = new SignUpRequest();
        signUp.setEmail(email);
        signUp.setName("Hun");
        signUp.setPwd(pwd);
        signUp.setPwdCheck(pwd);

        authService.signUp(signUp);

        SignInRequest signIn = new SignInRequest();
        signIn.setEmail(email);
        signIn.setPwd(pwd);

        SignInResponse response = authService.signIn(signIn);

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getUserId());

        String token = response.getAccessToken();
        assertTrue(jwtUtil.isValidToken(token));

        Long idFromToken = jwtUtil.getUserIdFromToken(token);
        String roleFromToken = jwtUtil.getRoleFromToken(token);

        assertEquals(response.getUserId(), idFromToken, "같다");
        assertThat(roleFromToken).isEqualTo("USER");

        var saved = userRepository.findByEmail(email).orElseThrow();
        assertEquals(saved.getId(), idFromToken);
    }
}

