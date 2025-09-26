package com.imfine.ngs.user.service;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.user.dto.request.SignInRequest;
import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.dto.response.SignInResponse;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (!request.getPwd().equals(request.getPwdCheck())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        User user = User.create(request.getEmail(), passwordEncoder.encode(request.getPwd()), request.getName(), null);
        var defaultRole = userRoleRepository.findByRole("USER").orElseThrow();
        var defaultStatus = userStatusRepository.findByName("ACTIVE").orElseThrow();
        user.assignRole(defaultRole);
        user.assignStatus(defaultStatus);
        userRepository.save(user);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new RuntimeException("Invalid credentials");
        }

        String role = user.getRole() != null ? user.getRole().getRole() : null;
        String token = jwtUtil.generateToken(user.getId(), role);
        return new SignInResponse(token, user.getId(), user.getEmail(), user.getNickname());
    }

    public void updatePwd(String email, String oldPwd, String newPwd) {
        if (oldPwd == null || newPwd == null) {
            throw new IllegalArgumentException("이전 비밀번호/새로운 비밀번호 미입력");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));
        if (!user.getPwd().equals(oldPwd)) {
            throw new IllegalArgumentException("이전 비밀번호와 동일 하지않음");
        }

        user.updatePassword(newPwd);
        userRepository.save(user);
    }
}
