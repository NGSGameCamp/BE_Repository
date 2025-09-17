package com.imfine.ngs.user.oauth.client;

import com.imfine.ngs.user.oauth.dto.OauthUserInfo;

public interface OauthClient {
    OauthUserInfo getUserInfo(String provider, String accessToken);
}
