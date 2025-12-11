package sw.blog.blogbackend.file.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageProvider {
  String upload(MultipartFile file, String path) throws IOException;

  void delete(String fileUrl);
}
