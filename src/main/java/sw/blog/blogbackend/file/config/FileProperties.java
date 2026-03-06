package sw.blog.blogbackend.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 파일 업로드 관련 설정 관리 클래스.
 */
@Component
@ConfigurationProperties(prefix = "upload")
@Getter
@Setter
public class FileProperties {
  private final Path path = new Path();

  @Getter
  @Setter
  public static class Path {
    private String local;
  }
}
