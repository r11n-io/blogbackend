package sw.blog.blogbackend.file.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.config.FileProperties;

/**
 * 로컬 스토리지 제공자 클래스.<br>
 * 개발 환경에서 파일을 로컬 디스크에 저장하기 위한 구현체.
 */
@Slf4j
@Service
@Profile("dev")
@RequiredArgsConstructor
public class LocalStorageProvider implements StorageProvider {

  private final FileProperties fileProperties;

  /**
   * 파일 업로드
   * 
   * @param file 업로드할 파일
   * @param path 업로드 경로
   * @return 업로드된 파일의 URL
   * @throws IOException 파일 저장 중 발생 예외
   */
  @SuppressWarnings("null")
  @Override
  public String upload(MultipartFile file, String path) throws IOException {
    String localUploadPath = fileProperties.getPath().getLocal();
    Path uploadDirPath = Paths.get(localUploadPath, path);

    Files.createDirectories(uploadDirPath);

    String extension = file.getOriginalFilename()
        .substring(file.getOriginalFilename().lastIndexOf("."));
    String savedFileName = UUID.randomUUID().toString() + extension;

    Path targetPath = uploadDirPath.resolve(savedFileName);

    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    return "/files/" + path + "/" + savedFileName;
  };

  /**
   * 파일 삭제
   * 
   * @param fileUrl 삭제할 파일의 URL
   */
  @Override
  public void delete(String fileUrl) {
    try {
      String localUploadPath = fileProperties.getPath().getLocal();
      String relativePath = fileUrl.replace("/files/", "");
      Path targetPath = Paths.get(localUploadPath, relativePath);

      boolean isDeleted = Files.deleteIfExists(targetPath);

      if (isDeleted) {
        log.info("로컬 파일 삭제 성공: ", targetPath);
      } else {
        log.info("로컬 파일 삭제 성공: ", targetPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("로컬 파일 삭제 실패: " + fileUrl, e);
    }
  };
}
