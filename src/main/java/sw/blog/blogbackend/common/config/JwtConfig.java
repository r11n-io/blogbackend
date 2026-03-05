package sw.blog.blogbackend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT 관련 설정을 외부화하여 관리하는 클래스
 */
@Configuration
@ConfigurationProperties(prefix = "jwt") // jwt. 로 시작하는 설정 찾기
@Getter
@Setter
public class JwtConfig {

  /**
   * JWT 비밀 키
   */
  private String secret;

  /**
   * 액세스 토큰 만료 시간 (밀리초)
   */
  private long expirationMs;

}
