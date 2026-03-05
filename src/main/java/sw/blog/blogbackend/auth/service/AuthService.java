package sw.blog.blogbackend.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.auth.dto.LoginRequest;
import sw.blog.blogbackend.auth.dto.TokenDto;
import sw.blog.blogbackend.auth.entity.RefreshToken;
import sw.blog.blogbackend.common.security.JwtTokenProvider;
import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository;

/**
 * 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  /**
   * 로그인 및 토큰 생성
   * 
   * @param request 로그인 요청 DTO
   * @return 로그인 성공 시 토큰 정보
   */
  @Transactional
  public TokenDto authenticateAndGenerateToken(LoginRequest request) {
    // 인증 + 콘텍스트 저장
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 액세스 토큰
    String accessToken = jwtTokenProvider.createAccessToken(authentication);

    // 리프레시 토큰
    User loginUser = userRepository.findByEmail(request.getEmail())
        .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
        .orElseThrow(() -> new BadCredentialsException("비정상 인증 요청"));
    Long userId = loginUser.getId();
    RefreshToken refreshToken = refreshTokenService.createAndSaveRefreshToken(userId);

    return new TokenDto(accessToken, refreshToken.getToken(), userId);
  }

  /**
   * 리프레시 토큰으로 액세스 토큰 갱신
   * 
   * @param requestRefreshToken 리프레시 토큰
   * @return 새로운 액세스 토큰과 사용자 ID를 포함한 응답
   */
  @Transactional
  public TokenDto refreshToken(String requestRefreshToken) {
    RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
        .orElseThrow(() -> new BadCredentialsException("Refresh token이 테이블에 존재하지 않습니다."));

    refreshTokenService.verifyExpiration(refreshToken);

    Long userId = refreshToken.getUserId();
    if (userId == null) {
      throw new BadCredentialsException("Refresh token에 사용자ID가 누락되었습니다.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadCredentialsException("사용자가 테이블에 존재하지 않습니다."));
    String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail());

    return new TokenDto(
        newAccessToken,
        refreshToken.getToken(),
        refreshToken.getUserId());
  }

}
