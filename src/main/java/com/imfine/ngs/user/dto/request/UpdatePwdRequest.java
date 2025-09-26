package com.imfine.ngs.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePwdRequest {
    @NotBlank
    private String oldPwd;
    @NotBlank
    private String newPwd;
    private String newPwdCheck;
}

