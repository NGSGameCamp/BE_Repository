package com.imfine.ngs.payment.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PortOneTokenResponse {
    private String accessToken;
    private String refreshToken;
}
