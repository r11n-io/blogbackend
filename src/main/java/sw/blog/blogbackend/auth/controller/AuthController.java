package sw.blog.blogbackend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.auth.dto.LoginRequest;
import sw.blog.blogbackend.auth.dto.TokenRefreshRequest;
import sw.blog.blogbackend.auth.dto.TokenResponse;
import sw.blog.blogbackend.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> authenticateUser(
      @RequestBody LoginRequest loginRequest) {
    TokenResponse response = authService.authenticateAndGenerateToken(loginRequest);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refreshToken(
      @RequestBody TokenRefreshRequest tokenRefreshRequest) {
    TokenResponse response = authService.refreshToken(tokenRefreshRequest.getRefreshToken());

    return ResponseEntity.ok(response);
  }

}
