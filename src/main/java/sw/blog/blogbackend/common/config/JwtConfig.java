package sw.blog.blogbackend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "jwt") // jwt. 로 시작하는 설정 찾기
@Getter
@Setter
public class JwtConfig {
  private String secret;
  private long expirationMs;
}
