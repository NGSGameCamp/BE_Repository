package com.imfine.ngs.user.oauth;

import com.imfine.ngs._global.config.security.jwt.JwtUtil;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.service.SocialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final SocialService socialService;

    @Value("${jwt.cookie.same-site:None}")
    private String cookieSameSite;

    @Value("${jwt.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.max-age-seconds:21600}")
    private long cookieMaxAgeSeconds;

    private static String nvl(String s, String d) { return (s == null || s.isBlank()) ? d : s; }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oAuthToken)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported authentication type");
            return;
        }

        String provider = oAuthToken.getAuthorizedClientRegistrationId();
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        String email;
        String name;

        if (principal instanceof OidcUser oidcUser) {
            // Google OIDC
            email = nvl(oidcUser.getEmail(), (String) attributes.get("email"));
            name = nvl(oidcUser.getFullName(), (String) attributes.getOrDefault("name", "User"));
        } else {
            switch (provider.toLowerCase()) {
                case "google" -> {
                    email = (String) attributes.get("email");
                    name = (String) attributes.getOrDefault("name", "User");
                }
                case "kakao" -> {
                    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                    Map<String, Object> profile = kakaoAccount == null ? null : (Map<String, Object>) kakaoAccount.get("profile");
                    email = kakaoAccount == null ? null : (String) kakaoAccount.get("email");
                    name = profile == null ? null : (String) profile.get("nickname");
                }
                case "naver" -> {
                    Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");
                    email = responseMap == null ? null : (String) responseMap.get("email");
                    name = responseMap == null ? null : (String) responseMap.get("nickname");
                }
                default -> {
                    email = (String) attributes.get("email");
                    name = (String) attributes.getOrDefault("name", "User");
                }
            }
        }

        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by provider: " + provider);
            return;
        }
        if (name == null || name.isBlank()) {
            name = email.split("@")[0];
        }

        User user = socialService.upsertSocialUser(provider, email, name);
        String role = user.getRole() != null ? user.getRole().getRole() : null;
        String token = jwtUtil.generateToken(user.getId(), role);
        setAuthCookie(response, token);
        String redirect = successRedirectBase();
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirect);
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        StringBuilder cookie = new StringBuilder();
        cookie.append("ACCESS_TOKEN=").append(URLEncoder.encode(token, StandardCharsets.UTF_8));
        cookie.append("; Path=/");
        cookie.append("; Max-Age=").append(cookieMaxAgeSeconds);
        cookie.append("; HttpOnly");
        if (cookieSameSite != null && !cookieSameSite.isBlank()) {
            cookie.append("; SameSite=").append(cookieSameSite);
        }
        if (cookieSecure) cookie.append("; Secure");
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String successRedirectBase() {
        return System.getenv().getOrDefault("OAUTH2_REDIRECT_URL", "http://localhost:3000/auth/callback");
    }
}
