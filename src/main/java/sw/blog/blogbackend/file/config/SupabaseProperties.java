package sw.blog.blogbackend.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Supabase 스토리지 관련 설정 관리 클래스.<br>
 * Supabase의 S3 호환 스토리지와 통신하기 위한 설정을 관리.
 */
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
