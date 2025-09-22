package com.imfine.ngs.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private String email;
    private String name;
    private String nickname;
    private String profile_url;
    private LocalDate birth_at;
}
