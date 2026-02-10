package sw.blog.blogbackend.auth.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.auth.dto.LoginRequest;
import sw.blog.blogbackend.auth.dto.TokenDto;
import sw.blog.blogbackend.auth.dto.TokenResponse;
import sw.blog.blogbackend.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @SuppressWarnings("null")
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> authenticateUser(
      @RequestBody LoginRequest loginRequest) {
    TokenDto dto = authService.authenticateAndGenerateToken(loginRequest);
    TokenResponse response = new TokenResponse(dto.accessToken(), dto.userId());
    ResponseCookie cookie = ResponseCookie.from("refreshToken", dto.refreshToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofDays(7))
        .sameSite("None")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refreshToken(
      @CookieValue(name = "refreshToken") String refreshTokenCookie) {
    TokenDto dto = authService.refreshToken(refreshTokenCookie);
    TokenResponse response = new TokenResponse(dto.accessToken(), dto.userId());

    return ResponseEntity.ok(response);
  }

}
