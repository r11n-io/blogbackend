package sw.blog.blogbackend.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

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
