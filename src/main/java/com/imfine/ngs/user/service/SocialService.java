package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatusRepository userStatusRepository;
    private final OauthClient oauthClient;

    public User socialLogin(String provider, String accessToken) {
        OauthUserInfo userInfo = oauthClient.getUserInfo(provider, accessToken);

        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.create(
                            userInfo.getEmail(),
                            "SOCIAL",
                            userInfo.getName(),
                            null
                    );
                    var defaultRole = userRoleRepository.findByRole("USER").orElseThrow();
                    var defaultStatus = userStatusRepository.findByName("ACTIVE").orElseThrow();
                    newUser.assignRole(defaultRole);
                    newUser.assignStatus(defaultStatus);
                    return userRepository.save(newUser);
                });
    }
}
