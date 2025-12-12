package sw.blog.blogbackend.file.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public class CustomByteArrayMultipartFile implements MultipartFile {

  // TODO: 경고 처리..

  private final byte[] bytes;
  private final String name;
  private final String originalFilename;
  private final String contentType;

  @Override
  public String getName() {
    return name;
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

  @Override
  public byte[] getBytes() throws IOException {
    return bytes;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void transferTo(File desc) throws IOException, IllegalStateException {
    throw new UnsupportedOperationException("파일 저장은 CustomByteArrayMultipartFile 에서 지원하지 않음.");
  }
}
