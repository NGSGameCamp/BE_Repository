package com.imfine.ngs.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "portoneAuthApiClient", url = "https://api.portone.io")
public interface PortOneAuthApiClient {
    @PostMapping("/login/api-secret")
    PortOneTokenResponse getToken(@RequestBody PortOneTokenRequest request);
}
