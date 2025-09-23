package com.imfine.ngs.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {
    private String nickname;
    private String profileUrl;
    private String pwd;
}
