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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.imfine.ngs.user.oauth.CookieAuthorizationRequestRepository;

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
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http
            /*
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            );
            */
            .authorizeHttpRequests(auth -> auth
//                    .anyRequest().permitAll()
//                    .anyRequest().authenticated()
                      .requestMatchers("/api/auth/**", "/api/games/**").permitAll()
                      .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/u/*", "/api/follow/following/*").permitAll()
                      .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // 스웨거 경로 추가
                      .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


    http.oauth2Login(oauth -> oauth
            .authorizationEndpoint(ep -> ep.authorizationRequestRepository(cookieAuthorizationRequestRepository()))
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

  @Bean
  public CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new CookieAuthorizationRequestRepository();
  }
}
