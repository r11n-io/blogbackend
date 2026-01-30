package sw.blog.blogbackend.common.schdule;

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
public class CleanupSchedular {

  private final ImageService imageService;

  @Scheduled(cron = "0 30 3 * * *", zone = "Asia/Seoul")
  public void cleanupFilesJob() {
    log.info("[스케줄] 미사용 파일 삭제 잡");
    long startTime = System.currentTimeMillis();

    try {
      int count = imageService.cleanupUnusedFiles(startTime);
      long endTime = System.currentTimeMillis();

      log.info("[스케줄] 미사용 파일 [{}]개 삭제 완료. [{}]ms 소요.",
          count, (endTime - startTime));
    } catch (Exception e) {
      log.error("[스케줄] 미사용 파일 삭제 잡 실패", e);
    }
  }
}
