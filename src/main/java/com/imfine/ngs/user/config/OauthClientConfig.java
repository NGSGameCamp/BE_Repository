package com.imfine.ngs.user.config;

import com.imfine.ngs.user.oauth.client.DefaultOauthClient;
import com.imfine.ngs.user.oauth.client.OauthClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OauthClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public OauthClient oauthClient(RestTemplate restTemplate) {
        return new DefaultOauthClient(restTemplate);
    }
}
