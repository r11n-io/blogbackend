package sw.blog.blogbackend.file.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

  private final FileService fileService;
  private final FileRepository fileRepository;

  @Transactional
  public void updateFileUsage(List<String> newUrls, Long postId) {
    List<File> previousFiles = fileRepository.findByPostId(postId);
    List<File> filesToUse = newUrls.isEmpty() ? List.of() : fileRepository.findByUrlIn(newUrls);
    Set<String> newUrlSet = new HashSet<>();

    for (File file : filesToUse) {
      file.setPostId(postId);
      file.setUsed(true);
    }

    for (File file : previousFiles) {
      if (!newUrlSet.contains(file.getUrl())) {
        file.setPostId(null);
        file.setUsed(false);
      }
    }
  }

  public File uploadPostImage(MultipartFile file) {
    // TODO: 이미지 형식 검사
    // TODO: 이미지 리사이징, 압축 등

    return fileService.uploadAndSaveRecord(file, "post-images");
  }

  public void cleanupUnusedFiles(long olderThanHour) {
    log.info("--- 미사용 파일 정리 시작 ---");

    LocalDateTime threshold = LocalDateTime.now().minusHours(olderThanHour);

    // TODO: 삭제 대상 파일 조회 -> 파일 삭제 반복 호출 -> 컬럼 삭제

    log.info("--- 미사용 파일 [ nnn ] 건 정리 완료 ---");
  }
}
