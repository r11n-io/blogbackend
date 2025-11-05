package sw.blog.blogbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

  // 보호 API 접근 토큰 (단기)
  private String accessToken;

  // 액세스 토큰 만료 갱신 요청 토큰 (장기)
  private String refreshToken;

  // 사용자 ID 키
  private Long userId;
}
