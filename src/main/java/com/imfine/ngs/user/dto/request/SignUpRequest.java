package com.imfine.ngs.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    private String pwd;

    @NotBlank
    @Size(min = 8, max = 64)
    private String pwdCheck;

    @NotBlank
    @Size(max = 50)
    private String name;

    // Optional; validated in service to allow null but reject blank
    @Size(max = 30)
    private String nickname;
}
