package com.imfine.ngs._global.config.security;

import com.imfine.ngs.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;


@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null || user.getRole().getRole() == null) {
            return Collections.emptyList();
        }
        String role = user.getRole().getRole();
        //  @PreAuthorize("hasRole('ADMIN')"), @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")이런식으로 사용 ADMIN으로 되있으면 앞에 추가
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }


    @Override
    public String getPassword() {
        return user.getPwd();
    }

    @Override
    public String getUsername() {return user.getEmail();}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

