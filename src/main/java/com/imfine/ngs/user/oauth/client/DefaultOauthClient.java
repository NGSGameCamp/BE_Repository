package com.imfine.ngs.user.oauth.client;

import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class DefaultOauthClient implements OauthClient {

    private final RestTemplate restTemplate;

    public DefaultOauthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OauthUserInfo getUserInfo(String provider, String accessToken) {
        String p = provider == null ? "" : provider.toLowerCase();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            return switch (p) {
                case "google" -> fromGoogle(entity);
                case "kakao" -> fromKakao(entity);
                case "naver" -> fromNaver(entity);
                default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
            };
        } catch (RestClientException e) {
            throw new RuntimeException("OAuth userinfo fetch failed for " + provider, e);
        }
    }

    private OauthUserInfo fromGoogle(HttpEntity<Void> entity) {
        ResponseEntity<Map> resp = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> body = resp.getBody();
        String email = body == null ? null : (String) body.get("email");
        String name = body == null ? null : (String) body.getOrDefault("name", "");
        return new OauthUserInfo(email, name);
    }

    private OauthUserInfo fromKakao(HttpEntity<Void> entity) {
        ResponseEntity<Map> resp = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> body = resp.getBody();
        Map<String, Object> kakaoAccount = body == null ? null : (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = kakaoAccount == null ? null : (Map<String, Object>) kakaoAccount.get("profile");
        String email = kakaoAccount == null ? null : (String) kakaoAccount.get("email");
        String name = profile == null ? null : (String) profile.get("nickname");
        return new OauthUserInfo(email, name);
    }

    private OauthUserInfo fromNaver(HttpEntity<Void> entity) {
        ResponseEntity<Map> resp = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> body = resp.getBody();
        Map<String, Object> response = body == null ? null : (Map<String, Object>) body.get("response");
        String email = response == null ? null : (String) response.get("email");
        String name = response == null ? null : (String) response.get("nickname");
        return new OauthUserInfo(email, name);
    }
}

