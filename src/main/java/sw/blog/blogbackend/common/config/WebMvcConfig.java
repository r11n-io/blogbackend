package sw.blog.blogbackend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.file.config.FileProperties;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.cors.allowed-origins}")
  private String[] allowedOrigins;

  private final FileProperties fileProperties;

  /**
   * 로컬 파일 시스템에 저장된 파일을 제공하기 위한 리소스 핸들러 설정
   */
  @SuppressWarnings("null")
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/files/**")
        .addResourceLocations("file:" + fileProperties.getPath().getLocal());
  }

  @SuppressWarnings("null")
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
