package sw.blog.blogbackend.common.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import sw.blog.blogbackend.common.config.JwtConfig;

@Component
public class JwtTokenProvider {

  private final JwtConfig jwtConfig;
  private final Long refreshTokenExpirationMs = 1000L * 60 * 60 * 24 * 7; // 7일

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  // 시크릿 키 디코딩
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.jwtConfig.getSecret());

    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Jwt 토큰 생성
  public String createAccessToken(Authentication authentication) {
    String userIdentifier = authentication.getName();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationMs());

    return Jwts.builder()
        .setSubject(userIdentifier)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public String createAccessToken(String subject) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationMs());

    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // 토큰에서 사용자 식별자 추출
  public String getIdenfierFromJWT(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  // JWT 토큰 유효성 검증
  public boolean validateToken(String authToken) {
    // TODO: 로거 라이브러리 적용 로깅

    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(authToken);

      return true;
    } catch (SignatureException ex) {
      System.out.println("서명 검증 실패 (시크릿 키 불일치)");
    } catch (MalformedJwtException ex) {
      System.out.println("유효하지 않는 JWT 형식");
    } catch (UnsupportedJwtException ex) {
      System.out.println("지원되지 않는 JWT 형식");
    } catch (IllegalArgumentException ex) {
      System.out.println("JWT 클레임 문자열이 비어있음");
    }

    return false;
  }

  // 리프레쉬 토큰 생성
  public String createRefreshToken(String subject) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // 토큰 만료 시간 반환
  public Instant getRefreshTokenExpiryDate() {
    return Instant.now().plusMillis(refreshTokenExpirationMs);
  }
}
