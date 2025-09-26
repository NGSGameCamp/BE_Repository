package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.repository.UserRepository;
import com.imfine.ngs.user.repository.UserRoleRepository;
import com.imfine.ngs.user.repository.UserStatusRepository;
import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceEmailDuplicateUnitTest {

    @Mock private UserRepository userRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUpFailWhenEmailDuplicated() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("a@b.com");
        request.setName("Hun");
        request.setPwd("1234");
        request.setPwdCheck("1234");

        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

        // when/then
        assertThrows(IllegalArgumentException.class, () -> authService.signUp(request));
    }
}

