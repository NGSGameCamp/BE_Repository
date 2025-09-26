package com.imfine.ngs.user.oauth;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.entity.UserRole;
import com.imfine.ngs.user.service.SocialService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AuthenticationSuccessHandlerTest {

    private static final String TEST_SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    @DisplayName("성공 핸들러는 JWT HttpOnly 쿠키를 설정하고 프론트로 리다이렉트한다 (Kakao)")
    void onSuccessSetsCookieAndRedirects() throws ServletException, IOException {
        // given kakao-style attributes
        Map<String, Object> kakaoAccount = Map.of(
                "email", "user@test.com",
                "profile", Map.of("nickname", "UserNick")
        );
        Map<String, Object> attributes = Map.of(
                "id", 12345L,
                "kakao_account", kakaoAccount
        );
        OAuth2User principal = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
        OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
                principal,
                principal.getAuthorities(),
                "kakao"
        );

        // stub services
        SocialService socialService = Mockito.mock(SocialService.class);
        UserRole role = UserRole.builder().id(1L).role("USER").description("user").build();
        User user = User.create("user@test.com", "SOCIAL", "UserName", null);
        user.assignRole(role);
        Mockito.when(socialService.upsertSocialUser(Mockito.eq("kakao"), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(user);

        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        OAuth2AuthenticationSuccessHandler handler = new OAuth2AuthenticationSuccessHandler(jwtUtil, socialService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        handler.onAuthenticationSuccess(request, response, auth);

        // then
        assertThat(response.getStatus()).isEqualTo(302);
        String location = response.getHeader("Location");
        assertThat(location).isEqualTo("http://localhost:3000/auth/callback");

        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("ACCESS_TOKEN=");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("Path=/");
    }
}
