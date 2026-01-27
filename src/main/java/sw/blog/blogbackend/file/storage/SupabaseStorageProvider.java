package sw.blog.blogbackend.file.storage;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.file.config.FileProperties;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class SupabaseStorageProvider implements StorageProvider {

  private final FileProperties fileProperties;

  @Override
  public String upload(MultipartFile file, String path) throws IOException {
    return "/to/be/detemined";
  }

  @Override
  public void delete(String fileUrl) {
  }
}
