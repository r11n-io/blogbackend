package sw.blog.blogbackend.common.init;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.service.ImageService;

/**
 * 개발환경 파일 정리 배치 클래스<br>
 *
 * - 개발 환경에서 주기적으로 미사용 파일을 정리하는 역할
 * - @Profile("dev")로 개발 환경에서만 활성화
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@Order(200)
public class DevFileCleanup implements CommandLineRunner {

  private final ImageService imageService;

  /**
   * 개발환경 파일 정리 배치 실행
   *
   * @param args Command line arguments
   * @throws Exception 예외 발생 시
   */
  @Override
  public void run(String... args) throws Exception {
    log.info("개발환경 파일 정리 배치 시작");

    try {
      // 6시간 이상 지난 미사용 파일 삭제
      LocalDateTime threshold = LocalDateTime.now().minusHours(6);

      imageService.cleanupUnusedFiles(threshold);
    } catch (Exception e) {
      log.error("개발환경 파일 정리 중 오류: {}", e.getMessage(), e);
    }

    log.info("개발환경 파일 정리 배치 완료");
  };
}
