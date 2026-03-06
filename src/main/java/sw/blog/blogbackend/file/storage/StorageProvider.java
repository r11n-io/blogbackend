package sw.blog.blogbackend.file.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * 스토리지 제공자 인터페이스.<br>
 * 파일 업로드 및 삭제 기능을 정의하는 인터페이스.
 */
public interface StorageProvider {

  /**
   * 파일 업로드
   *
   * @param file 업로드할 파일
   * @param path 업로드 경로
   * @return 업로드된 파일의 URL
   * @throws IOException 파일 저장 중 발생 예외
   */
  String upload(MultipartFile file, String path) throws IOException;

  /**
   * 파일 삭제
   *
   * @param fileUrl 삭제할 파일의 URL
   */
  void delete(String fileUrl);
}
