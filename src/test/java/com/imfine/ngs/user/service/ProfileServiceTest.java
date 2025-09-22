package com.imfine.ngs.user.service;

import com.imfine.ngs.user.dto.request.ProfileRequest;
import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.dto.response.ProfileResponse;
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
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProfileServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("프로필 변경 성공")
    void updateProfileSuccess() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("a@b.com");
        request.setName("Hun");
        request.setPwd("1234");
        request.setPwdCheck("1234");

        authService.signUp(request);
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setNickname("NewHun");
        profileService.updateProfile("a@b.com", profileRequest);
        User updatedUser = userRepository.findByEmail("a@b.com").orElseThrow();
        assertEquals("NewHun", updatedUser.getNickname());

    }

    @Test
    @DisplayName("프로필 조회 성공")
    void GetProfileSuccess() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("a@b.com");
        request.setName("Hun");
        request.setPwd("1234");
        request.setPwdCheck("1234");

        authService.signUp(request);

        ProfileResponse profileResponse = profileService.getProfile("a@b.com");
        assertEquals("Hun", profileResponse.getNickname());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUserSuccess() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("a@b.com");
        request.setName("Hun");
        request.setPwd("1234");
        request.setPwdCheck("1234");

        authService.signUp(request);

        profileService.deleteUser("a@b.com");

        assertFalse(userRepository.findByEmail("a@b.com").isPresent());



    }
}

