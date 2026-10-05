package sw.blog.blogbackend.common.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 헬스체크 컨트롤러
 */
@RestController
public class HealthController {

  /**
   * 서비스 상태 확인
   *
   * @return 상태 정보
   */
  @GetMapping("/api/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }
}
