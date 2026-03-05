package sw.blog.blogbackend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.file.config.FileProperties;

/**
 * 웹 MVC 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.cors.allowed-origins}")
  private String[] allowedOrigins;

  private final FileProperties fileProperties;

  /**
   * 정적 리소스 핸들러 설정
   *
   * @param registry ResourceHandlerRegistry
   */
  @SuppressWarnings("null")
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/files/**")
        .addResourceLocations("file:" + fileProperties.getPath().getLocal());
  }

  /**
   * CORS 매핑 설정
   *
   * @param registry CorsRegistry
   */
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
