package com.imfine.ngs.payment.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class PortOneAuthInterceptor implements RequestInterceptor {

    private final PortOneAuthApiClient portOneAuthApiClient;
    private final String apiSecret;

    private String accessToken;
    private LocalDateTime tokenExpiry;

    @Override
    public void apply(RequestTemplate template) {
        if (template.url().equals("/login/api-secret")) {
            return;
        }

        if (accessToken == null || tokenExpiry == null || LocalDateTime.now().isAfter(tokenExpiry)) {
            refreshAccessToken();
        }
        template.header("Authorization", "Bearer " + accessToken);
    }

    private void refreshAccessToken() {
        PortOneTokenRequest request = new PortOneTokenRequest(apiSecret);
        PortOneTokenResponse response = portOneAuthApiClient.getToken(request);
        this.accessToken = response.getAccessToken();
        this.tokenExpiry = LocalDateTime.now().plusMinutes(25);
    }
}
