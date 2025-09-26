package com.imfine.ngs.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocialAuthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new SocialAuthController()).build();
    }

    @Test
    @DisplayName("/api/auth/login/google는 provider authorization URL로 302 리다이렉트한다")
    void redirectToGoogle() throws Exception {
        mockMvc.perform(get("/api/auth/login/google"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/oauth2/authorization/google")));
    }

    @Test
    @DisplayName("/api/auth/login/kakao는 provider authorization URL로 302 리다이렉트한다")
    void redirectToKakao() throws Exception {
        mockMvc.perform(get("/api/auth/login/kakao"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/oauth2/authorization/kakao")));
    }
}
