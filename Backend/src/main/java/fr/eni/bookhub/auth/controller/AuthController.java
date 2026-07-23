package fr.eni.bookhub.auth.controller;

import fr.eni.bookhub.auth.dto.request.LoginRequest;
import fr.eni.bookhub.auth.dto.request.RegisterRequest;
import fr.eni.bookhub.auth.dto.response.LoginResponse;
import fr.eni.bookhub.auth.dto.response.RegisterResponse;
import fr.eni.bookhub.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final long REFRESH_COOKIE_MAX_AGE_SECONDS = 7 * 24 * 60 * 60;

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        attachRefreshCookie(response, loginResponse.getRefreshToken());
        return ResponseEntity.ok(stripRefreshToken(loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefreshToken = extractRefreshCookie(request);

        if (rawRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LoginResponse refreshed = authService.refresh(rawRefreshToken);
        attachRefreshCookie(response, refreshed.getRefreshToken());
        return ResponseEntity.ok(stripRefreshToken(refreshed));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String rawRefreshToken = extractRefreshCookie(request);
        authService.logout(rawRefreshToken);
        clearRefreshCookie(response);
        return ResponseEntity.ok().build();
    }

    private void attachRefreshCookie(HttpServletResponse response, String rawRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, rawRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(REFRESH_COOKIE_MAX_AGE_SECONDS)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private LoginResponse stripRefreshToken(LoginResponse response) {
        return LoginResponse.builder()
                .id(response.getId())
                .firstname(response.getFirstname())
                .lastname(response.getLastname())
                .username(response.getUsername())
                .phone(response.getPhone())
                .email(response.getEmail())
                .role(response.getRole())
                .accessToken(response.getAccessToken())
                .build();
    }
}
