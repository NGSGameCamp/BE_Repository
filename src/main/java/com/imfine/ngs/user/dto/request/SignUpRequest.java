package com.imfine.ngs.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String email;
    private String pwd;
    private String pwdCheck;
    private String name;
}
