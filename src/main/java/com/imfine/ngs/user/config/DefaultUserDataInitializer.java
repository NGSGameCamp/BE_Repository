package com.imfine.ngs.user.config;

import com.imfine.ngs.user.entity.UserRole;
import com.imfine.ngs.user.entity.UserStatus;
import com.imfine.ngs.user.repository.UserRoleRepository;
import com.imfine.ngs.user.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DefaultUserDataInitializer {

    private final UserRoleRepository userRoleRepository;
    private final UserStatusRepository userStatusRepository;

    @Bean
    @Transactional
    public ApplicationRunner initDefaultUserData() {
        return args -> {
            userRoleRepository.findByRole("USER").orElseGet(() ->
                    userRoleRepository.save(UserRole.builder()
                            .role("USER")
                            .description("Default user role")
                            .build())
            );

            userStatusRepository.findByName("ACTIVE").orElseGet(() ->
                    userStatusRepository.save(UserStatus.builder()
                            .name("ACTIVE")
                            .description("Active user status")
                            .build())
            );
        };
    }
}

