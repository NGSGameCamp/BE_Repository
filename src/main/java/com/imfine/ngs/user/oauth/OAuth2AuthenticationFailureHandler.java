package com.imfine.ngs.user.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String base = System.getenv().getOrDefault("OAUTH2_REDIRECT_URL", "http://localhost:3000/auth/callback");
        String redirect = base + (base.contains("?") ? "&" : "?") + "error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirect);
    }
}

