package sw.blog.blogbackend.file.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;
import sw.blog.blogbackend.file.storage.StorageProvider;

/**
 * 파일 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;
  private final StorageProvider storageProvider;

  /**
   * 파일 업로드 및 메타데이터 저장
   *
   * @param file       업로드할 파일
   * @param uploadPath 파일 업로드 경로
   * @return 저장된 파일 엔티티
   */
  @SuppressWarnings("null")
  @Transactional
  public File uploadAndSaveRecord(MultipartFile file, String uploadPath) {
    try {
      // 물리파일 업로드
      String fileUrl = storageProvider.upload(file, uploadPath);

      File newFile = File.builder()
          .url(fileUrl)
          .originalFileName(file.getOriginalFilename())
          .fileSize(file.getSize())
          .mimeType(file.getContentType())
          .isUsed(false)
          .postId(null)
          .build();

      return fileRepository.save(newFile);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
  }

}
