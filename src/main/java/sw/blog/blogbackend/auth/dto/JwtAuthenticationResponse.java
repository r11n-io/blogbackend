package sw.blog.blogbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtAuthenticationResponse {

  public JwtAuthenticationResponse(String accessToken) {
    this.accessToken = accessToken;
  }

  private String accessToken;

  private String tokenType = "Bearer"; // HTTP 헤더 토큰타입

}
