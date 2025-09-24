package com.imfine.ngs._global.config.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * authenticationPrincipal사용을 위해 추가 userDetailService는 db에서 조회했던 거고 이건 토큰에서 가져오는겁니다.
 * 기본 인증/인가 - principal, 중요 api - userDetail
 */
public class JwtUserPrincipal {
    private final Long userId;
    private final String role; // e.g., USER, ADMIN, PUBLISHER

    public JwtUserPrincipal(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public String getRole() { return role; }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isBlank()) return List.of();
        String name = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(name));
    }
}

