package sw.blog.blogbackend.common.schdule;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.service.ImageService;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CleanupScheduler {

  private final ImageService imageService;

  @Scheduled(cron = "0 30 3 * * *", zone = "Asia/Seoul")
  public void cleanupFilesJob() {
    log.info("[스케줄] 미사용 파일 삭제 잡");
    long startProcessTime = System.currentTimeMillis();

    try {
      LocalDateTime threshold = LocalDateTime.now().minusDays(1);
      int count = imageService.cleanupUnusedFiles(threshold);
      long endProcessTime = System.currentTimeMillis();

      log.info("[스케줄] 미사용 파일 [{}]개 삭제 완료. [{}]ms 소요.",
          count, (endProcessTime - startProcessTime));
    } catch (Exception e) {
      log.error("[스케줄] 미사용 파일 삭제 잡 실패", e);
    }
  }
}
