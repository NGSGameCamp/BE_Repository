package com.imfine.ngs.user.service;

import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import com.imfine.ngs.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SocialServiceTest {

    @InjectMocks
    private SocialService socialService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OauthClient oauthClient;

    @Test
    @DisplayName("소셜 로그인 - 신규 유저 생성")
    void socialSignUpNewUser() {
        OauthUserInfo info = new OauthUserInfo("a@b.com", "Hun");

        given(oauthClient.getUserInfo("google", "token"))
                .willReturn(info);
        given(userRepository.findByEmail("a@b.com"))
                .willReturn(Optional.empty());
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        User user = socialService.socialLogin("google", "token");

        assertEquals("a@b.com", user.getEmail());
        assertEquals("Hun", user.getNickname());

    }

    @Test
    @DisplayName("소셜 로그인 - 기존 유저 로그인 성공")
    void socialsignInSuccess() {
        User socialUser = User.create("a@b.com", "SOCIAL", "Hun", null);

        given(oauthClient.getUserInfo("google", "validtoken"))
                .willReturn(new OauthUserInfo("a@b.com", "Hun"));
        given(userRepository.findByEmail("a@b.com"))
                .willReturn(Optional.of(socialUser));

        User user = socialService.socialLogin("google", "validtoken");

        assertEquals(socialUser.getNickname(), user.getNickname());
    }

    @Test
    @DisplayName("소셜 로그인 실패 - 잘못된 토큰")
    void socialsignInFail() {
        given(oauthClient.getUserInfo("google", "Invalidtoken"))
                .willThrow(new IllegalArgumentException("Invalid token"));

        assertThrows(IllegalArgumentException.class, () -> socialService.socialLogin("google", "Invalidtoken"));

    }
}
