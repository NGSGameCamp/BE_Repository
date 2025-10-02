package com.imfine.ngs.user.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("${app.oauth2.redirect-url}")
    private String oauth2RedirectUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // Clean authorization request cookies to avoid stale state on next login
        CookieAuthorizationRequestRepository.clearAuthorizationCookies(response);

        String base = oauth2RedirectUrl;
        String redirect = base + (base.contains("?") ? "&" : "?") + "error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirect);
    }
}
