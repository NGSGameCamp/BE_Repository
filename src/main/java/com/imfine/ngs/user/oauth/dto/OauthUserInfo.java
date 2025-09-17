package com.imfine.ngs.user.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthUserInfo {
    private String email;
    private String name;
}
