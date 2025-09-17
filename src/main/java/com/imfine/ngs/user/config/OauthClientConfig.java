package com.imfine.ngs.user.config;

import com.imfine.ngs.user.oauth.client.OauthClient;
import com.imfine.ngs.user.oauth.dto.OauthUserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OauthClientConfig {

    @Bean
    public OauthClient oauthClient() {
        return (provider, token) -> new OauthUserInfo("dummy@test.com", "DummyUser");
    }
}
