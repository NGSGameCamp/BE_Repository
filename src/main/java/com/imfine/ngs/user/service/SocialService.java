package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatusRepository userStatusRepository;
    private final OauthClient oauthClient;

    @lombok.Value
    public static class UpsertResult {
        Long userId;
        String role;
    }

    public User socialLogin(String provider, String accessToken) {
        OauthUserInfo userInfo = oauthClient.getUserInfo(provider, accessToken);

        return upsertSocialUser(provider, userInfo.getEmail(), userInfo.getName());
    }

    public User upsertSocialUser(String provider, String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.create(
                            email,
                            "SOCIAL",
                            name,
                            null
                    );
                    var defaultRole = userRoleRepository.findByRole("USER")
                            .orElseGet(() -> userRoleRepository.save(new com.imfine.ngs.user.entity.UserRole(null, "USER", "Default user role")));
                    var defaultStatus = userStatusRepository.findByName("ACTIVE")
                            .orElseGet(() -> userStatusRepository.save(new com.imfine.ngs.user.entity.UserStatus(null, "ACTIVE", "Active user status")));
                    newUser.assignRole(defaultRole);
                    newUser.assignStatus(defaultStatus);
                    return userRepository.save(newUser);
                });
    }

    /**
     * Upsert user and return minimal info required by web layer within a transactional boundary
     * to avoid LazyInitializationException on User.role.
     */
    @Transactional
    public UpsertResult upsertSocialUserWithRole(String provider, String email, String name) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.create(
                            email,
                            "SOCIAL",
                            name,
                            null
                    );
                    var defaultRole = userRoleRepository.findByRole("USER")
                            .orElseGet(() -> userRoleRepository.save(new com.imfine.ngs.user.entity.UserRole(null, "USER", "Default user role")));
                    var defaultStatus = userStatusRepository.findByName("ACTIVE")
                            .orElseGet(() -> userStatusRepository.save(new com.imfine.ngs.user.entity.UserStatus(null, "ACTIVE", "Active user status")));
                    newUser.assignRole(defaultRole);
                    newUser.assignStatus(defaultStatus);
                    return userRepository.save(newUser);
                });

        String role = user.getRole() != null ? user.getRole().getRole() : null;
        return new UpsertResult(user.getId(), role);
    }
}
