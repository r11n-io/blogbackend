package sw.blog.blogbackend.file.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("dev")
public class LocalStorageProvider implements StorageProvider {

  @Value("${upload.path.local}")
  private String localUploadPath;

  @SuppressWarnings("null")
  @Override
  public String upload(MultipartFile file, String path) throws IOException {
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

  @Override
  public void delete(String fileUrl) {
    try {
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
