package com.imfine.ngs.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {
    private String accessToken;
    private Long userId;

    public SignInResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
