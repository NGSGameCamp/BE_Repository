package com.imfine.ngs.user.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.user.dto.request.SignInRequest;
import com.imfine.ngs.user.dto.request.SignUpRequest;
import com.imfine.ngs.user.dto.request.UpdatePwdRequest;
import com.imfine.ngs.user.dto.response.SignInResponse;
import com.imfine.ngs.user.dto.response.WhoAmIResponse;
import com.imfine.ngs.user.dto.response.EmailAvailabilityResponse;
import com.imfine.ngs.user.service.AuthService;
import com.imfine.ngs.user.repository.UserRepository;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import com.imfine.ngs.user.oauth.CookieAuthorizationRequestRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

    @Value("${jwt.cookie.same-site}")
    private String cookieSameSite;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.max-age-seconds}")
    private long cookieMaxAgeSeconds;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request, HttpServletResponse response) {
        SignInResponse body = authService.signIn(request);
        // Issue HttpOnly cookie as well (header auth still supported and takes precedence)
        StringBuilder cookie = new StringBuilder();
        cookie.append("ACCESS_TOKEN=").append(body.getAccessToken());
        cookie.append("; Path=/");
        cookie.append("; Max-Age=").append(cookieMaxAgeSeconds);
        cookie.append("; HttpOnly");
        if (cookieSameSite != null && !cookieSameSite.isBlank()) {
            cookie.append("; SameSite=").append(cookieSameSite);
        }
        if (cookieSecure) cookie.append("; Secure");
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/email/check")
    public ResponseEntity<EmailAvailabilityResponse> checkEmail(@RequestParam("email") @Email String email) {
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(new EmailAvailabilityResponse(email, available));
    }

    

    @PutMapping("/updatePwd")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal JwtUserPrincipal principal,
                                               @RequestBody @Valid UpdatePwdRequest request) {
        if (request.getNewPwdCheck() != null && !request.getNewPwd().equals(request.getNewPwdCheck())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        authService.updatePasswordByUserId(principal.getUserId(), request.getOldPwd(), request.getNewPwd());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WhoAmIResponse> whoAmI(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        String role = user.getRole() != null ? user.getRole().getRole() : null;
        WhoAmIResponse body = new WhoAmIResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getProfileUrl(),
                role
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletResponse response) {
        // Clear security context (stateless app but good hygiene)
        SecurityContextHolder.clearContext();

        // Expire ACCESS_TOKEN cookie
        StringBuilder cookie = new StringBuilder();
        cookie.append("ACCESS_TOKEN=");
        cookie.append("; Path=/");
        cookie.append("; Max-Age=0");
        cookie.append("; HttpOnly");
        if (cookieSameSite != null && !cookieSameSite.isBlank()) {
            cookie.append("; SameSite=").append(cookieSameSite);
        }
        if (cookieSecure) cookie.append("; Secure");
        response.addHeader("Set-Cookie", cookie.toString());

        // Clear any OAuth2 authorization cookies to avoid stale state
        CookieAuthorizationRequestRepository.clearAuthorizationCookies(response);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signout")
    public ResponseEntity<Void> signOutGet(HttpServletResponse response) {
        return signOut(response);
    }

}
