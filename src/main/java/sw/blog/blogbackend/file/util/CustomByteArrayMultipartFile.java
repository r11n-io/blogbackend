package sw.blog.blogbackend.file.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

/**
 * byte 배열을 기반으로 MultipartFile 인터페이스를 구현한 클래스.<br>
 * 이 클래스는 파일 업로드 시 byte 배열로 파일 데이터를 처리할 수 있도록 함.
 */
@Builder
public class CustomByteArrayMultipartFile implements MultipartFile {

  private final byte[] bytes;
  private final String name;
  private final String originalFilename;
  private final String contentType;

  @NonNull
  @Override
  public String getName() {
    return (this.name != null) ? this.name : "";
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return bytes.length == 0;
  }

  @Override
  public long getSize() {
    return bytes.length;
  }

  @NonNull
  @Override
  public byte[] getBytes() throws IOException {
    return (bytes != null) ? bytes : new byte[0];
  }

  @NonNull
  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void transferTo(@NonNull File desc) throws IOException, IllegalStateException {
    throw new UnsupportedOperationException("파일 저장은 CustomByteArrayMultipartFile 에서 지원하지 않음.");
  }
}
