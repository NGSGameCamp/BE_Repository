package com.imfine.ngs.payment.config;

import com.imfine.ngs.payment.client.PortOneAuthApiClient;
import com.imfine.ngs.payment.client.PortOneAuthInterceptor;
import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${portone.api-secret}")
    private String apiSecret;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public PortOneAuthInterceptor portOneAuthInterceptor(PortOneAuthApiClient portOneAuthApiClient) {
        return new PortOneAuthInterceptor(portOneAuthApiClient, apiSecret);
    }
}
