package com.imfine.ngs._global.config.security;

import com.imfine.ngs._global.config.security.jwt.JwtAuthenticationFilter;
import com.imfine.ngs.user.oauth.OAuth2AuthenticationFailureHandler;
import com.imfine.ngs.user.oauth.OAuth2AuthenticationSuccessHandler;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

    /* todo: 권한에 따른 접근 권한 설정 하기
     *  현재 주석 처리 된 부분 활성화하시고 아래코드 주석처리하시면 필터랑 인증 사라집니다. 현재 /auth로 매핑된 로그인/회원가입만 허용되어서 나중에 비회원으로 할 수 있는 내용들 추가 검토 후 추가하겠습니다.
     */

    http
            /*
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            );
            */
            .authorizeHttpRequests(auth -> auth
//                    .anyRequest().permitAll()
//                    .anyRequest().authenticated()
                      .requestMatchers("/api/auth/**", "/api/main").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/u/*", "/api/follow/following/*").permitAll()
                      .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // 스웨거 경로 추가
                      .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


    http.oauth2Login(oauth -> oauth
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler)
    );


    return http.build();
  }

  @Bean
  @Profile({"test", "dev"})
  public WebSecurityCustomizer h2ConsoleCustomizer() {
    return web -> web.ignoring().requestMatchers("/h2-console/**");
  }
}
