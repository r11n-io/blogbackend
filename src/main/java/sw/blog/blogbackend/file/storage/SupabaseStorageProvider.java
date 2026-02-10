package sw.blog.blogbackend.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import sw.blog.blogbackend.file.config.SupabaseProperties;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class SupabaseStorageProvider implements StorageProvider {

  private final S3Client s3Client;
  private final SupabaseProperties supabaseProperties;

  @SuppressWarnings("null")
  @Override
  public String upload(MultipartFile file, String path) throws IOException {
    String originalName = file.getOriginalFilename();
    String extension = originalName.substring(originalName.lastIndexOf("."));
    String savedFileName = UUID.randomUUID().toString() + extension;
    String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    String fullPath = String.format("%s/%s/%s", path, datePath, savedFileName);

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(supabaseProperties.getBucket())
        .key(fullPath)
        .contentType(file.getContentType())
        .build();

    try (InputStream is = file.getInputStream()) {
      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(is, file.getSize()));
    } catch (IOException e) {
      throw new RuntimeException("Upload failed", e);
    }

    return String.format("%s/object/public/%s/%s",
        supabaseProperties.getEndpoint().replace("/s3", ""),
        supabaseProperties.getBucket(),
        fullPath);
  }

  @Override
  public void delete(String fileUrl) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(supabaseProperties.getBucket())
        .key(getSupabaseKey(fileUrl))
        .build();

    try {
      s3Client.deleteObject(deleteObjectRequest);
    } catch (S3Exception e) {
      throw new RuntimeException("파일 삭제 중 오류 발생: " + e.awsErrorDetails().errorMessage());
    }
  }

  private String getSupabaseKey(String url) {
    if (url.isEmpty())
      return null;

    if (!url.startsWith("http"))
      return url;

    String target = "post-images/";
    int idx = url.indexOf(target);

    return (idx != -1) ? url.substring(idx) : url;
  }
}
