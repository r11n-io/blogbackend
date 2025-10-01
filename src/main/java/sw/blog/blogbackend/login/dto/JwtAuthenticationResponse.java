package sw.blog.blogbackend.login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
  private String accessToken;
  private String tokenType = "Bearer";  // HTTP 헤더 토큰타입

  public JwtAuthenticationResponse(String accessToken) {
    this.accessToken = accessToken;
  }
}
