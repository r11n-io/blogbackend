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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public void deleteExistingToken(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  @Transactional
  public RefreshToken createAndSaveRefreshToken(Long userId) {
    // refreshTokenRepository.deleteByUserId(userId);
    deleteExistingToken(userId);

    String newRefreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userId));
    Instant expiryDate = jwtTokenProvider.getRefreshTokenExpiryDate();
    RefreshToken refreshToken = new RefreshToken(null, userId, newRefreshToken, expiryDate);

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new BadCredentialsException("Refresh token이 만료되었습니다.");
    }

    return token;
  }

}
