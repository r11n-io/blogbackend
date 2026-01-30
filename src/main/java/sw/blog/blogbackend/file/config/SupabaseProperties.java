package sw.blog.blogbackend.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "supabase.storage")
@Getter
@Setter
public class SupabaseProperties {
  private String bucket;
  private String endpoint;
  private String region;
  private String accessKey;
  private String secretKey;
}
