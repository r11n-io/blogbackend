package sw.blog.blogbackend.common.security;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import sw.blog.blogbackend.common.config.JwtConfig;

@Component
public class JwtTokenProvider {

  private final JwtConfig jwtConfig;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  // 시크릿 키 디코딩
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.jwtConfig.getSecret());

    return Keys.hmacShaKeyFor(keyBytes);
  }

  // FIXME: 진행중.. Jwt 토큰 생성
  // public String generateToken(Authentication authentication) {
  //   String userIdentifier;
  // }
}
