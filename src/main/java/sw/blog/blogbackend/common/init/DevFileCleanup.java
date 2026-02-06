package sw.blog.blogbackend.common.init;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.service.ImageService;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@Order(200)
public class DevFileCleanup implements CommandLineRunner {

  private final ImageService imageService;

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
