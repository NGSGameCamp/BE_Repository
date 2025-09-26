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

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPwd()),
                request.getName(),
                request.getNickname()
        );
        var defaultRole = userRoleRepository.findByRole("USER").orElseThrow();
        var defaultStatus = userStatusRepository.findByName("ACTIVE").orElseThrow();
        user.assignRole(defaultRole);
        user.assignStatus(defaultStatus);
        userRepository.save(user);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean isNicknameAvailable(String nickname) {return !userRepository.existsByNickname(nickname);}

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

    public void updatePwdByUserId(Long userId, String oldPwd, String newPwd) {
        if (oldPwd == null || oldPwd.isBlank() || newPwd == null || newPwd.isBlank()) {
            throw new IllegalArgumentException("이전/새 비밀번호를 입력해주세요.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if (!passwordEncoder.matches(oldPwd, user.getPwd())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(newPwd, user.getPwd())) {
            throw new IllegalArgumentException("새 비밀번호가 기존 비밀번호와 동일합니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPwd));
        userRepository.save(user);
    }
}
