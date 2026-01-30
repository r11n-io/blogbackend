package sw.blog.blogbackend.file.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

  private final SupabaseProperties supabaseProperties;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .endpointOverride(URI.create(supabaseProperties.getEndpoint()))
        .region(Region.of(supabaseProperties.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                supabaseProperties.getAccessKey(),
                supabaseProperties.getSecretKey())))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build())
        .build();
  }

}
