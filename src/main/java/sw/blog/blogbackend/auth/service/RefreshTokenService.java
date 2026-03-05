package sw.blog.blogbackend.auth.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.auth.entity.RefreshToken;
import sw.blog.blogbackend.auth.repository.RefreshTokenRepository;
import sw.blog.blogbackend.common.security.JwtTokenProvider;

/**
 * 리프레시 토큰 서비스
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 새로운 리프레시 토큰 생성 및 저장
   *
   * @param userId 사용자 ID
   * @return 생성된 리프레시 토큰 엔티티
   */
  @Transactional
  public RefreshToken createAndSaveRefreshToken(Long userId) {
    deleteExistingToken(userId);

    String newRefreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userId));
    Instant expiryDate = jwtTokenProvider.getRefreshTokenExpiryDate();
    RefreshToken refreshToken = new RefreshToken(null, userId, newRefreshToken, expiryDate);

    return refreshTokenRepository.save(refreshToken);
  }

  /**
   * 토큰 문자열로 리프레시 토큰 조회
   *
   * @param token 리프레시 토큰 문자열
   * @return RefreshToken 엔티티
   */
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  /**
   * 리프레시 토큰 만료 확인
   *
   * @param token 리프레시 토큰
   * @return 만료되지 않은 리프레시 토큰
   */
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);

      throw new BadCredentialsException("Refresh token이 만료되었습니다.");
    }

    return token;
  }

  /**
   * 사용자 ID로 기존 리프레시 토큰 삭제
   *
   * @param userId 사용자 ID
   */
  @Transactional
  public void deleteExistingToken(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

}
