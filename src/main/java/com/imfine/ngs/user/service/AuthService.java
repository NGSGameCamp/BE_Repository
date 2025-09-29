package com.imfine.ngs.user.service;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
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
        final String email = request.getEmail().trim();
        final String name = request.getName().trim();
        final String nickname = request.getNickname() == null ? null : request.getNickname().trim();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_EMAIL_DUPLICATE);
        }
        if (!request.getPwd().equals(request.getPwdCheck())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = User.create(
                email,
                passwordEncoder.encode(request.getPwd()),
                name,
                nickname
        );
        var defaultRole = userRoleRepository.findByRole("USER")
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        var defaultStatus = userStatusRepository.findByName("ACTIVE")
                .orElseThrow(() -> new BusinessException(ErrorCode.STATUS_NOT_FOUND));
        user.assignRole(defaultRole);
        user.assignStatus(defaultStatus);
        userRepository.save(user);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public SignInResponse signIn(SignInRequest request) {
        final String email = request.getEmail().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        String role = user.getRole() != null ? user.getRole().getRole() : null;
        String token = jwtUtil.generateToken(user.getId(), role);
        return new SignInResponse(token, user.getId(), user.getEmail(), user.getNickname());
    }

    public void updatePasswordByUserId(Long userId, String oldPwd, String newPwd) {
        if (oldPwd == null || oldPwd.isBlank() || newPwd == null || newPwd.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!passwordEncoder.matches(oldPwd, user.getPwd())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        if (passwordEncoder.matches(newPwd, user.getPwd())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        user.updatePassword(passwordEncoder.encode(newPwd));
        userRepository.save(user);
    }
}
