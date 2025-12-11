package sw.blog.blogbackend.file.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;
import sw.blog.blogbackend.file.storage.StorageProvider;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;
  private final StorageProvider storageProvider;

  @SuppressWarnings("null")
  public File uploadAndSaveRecord(MultipartFile file, String uploadPath) {
    try {
      String fileUrl = storageProvider.upload(file, uploadPath);

      File newFile = File.builder()
          .url(fileUrl)
          .isUsed(false)
          .build();

      return fileRepository.save(newFile);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
  }
}
