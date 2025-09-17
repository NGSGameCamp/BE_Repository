package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final UserRepository userRepository;
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
                    return userRepository.save(newUser);
                });
    }
}

