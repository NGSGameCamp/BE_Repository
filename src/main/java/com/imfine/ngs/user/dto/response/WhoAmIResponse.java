package com.imfine.ngs.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WhoAmIResponse {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String profileUrl;
    private String role;
}

