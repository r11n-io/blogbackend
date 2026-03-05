package sw.blog.blogbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

  /**
   * 보호 API 접근 토큰 (단기)
   */
  private String accessToken;

  /**
   * 사용자 ID (액세스 토큰과 함께 반환, 보호 API 접근 시 필요)
   */
  private Long userId;
}
