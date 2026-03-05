package sw.blog.blogbackend.common.constants;

/**
 * JWT 토큰 관련 상수 정의 클래스
 */
public class JwtTokenConstants {

  /**
   * 인스턴스 생성 방지
   */
  private JwtTokenConstants() {
  }

  /**
   * JWT 토큰 헤더 접두사
   */
  public static final String TOKEN_PREFIX = "Bearer ";

}
