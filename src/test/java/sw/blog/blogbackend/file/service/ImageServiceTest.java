package sw.blog.blogbackend.file.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import sw.blog.blogbackend.file.config.FileProperties;

@SpringBootTest
@ActiveProfiles("dev")
public class ImageServiceTest {

  @Autowired
  private ImageService imageService;

  @Autowired
  private FileProperties fileProperties;

  @Test
  void uploadLocalFileTest() throws IOException {
    // 1. 진짜 1x1 픽셀 이미지 바이너리 생성
    BufferedImage whiteImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    javax.imageio.ImageIO.write(whiteImage, "jpg", baos);
    byte[] imageBytes = baos.toByteArray();
    MockMultipartFile mockFile = new MockMultipartFile(
        "test-image.jpg",
        "test-image.jpg",
        "image/jpeg",
        imageBytes);
    String returnedUrl = imageService.uploadPostImage(mockFile).getUrl();

    System.out.println("업로드된 파일 URL: " + returnedUrl);

    assertThat(returnedUrl).contains("/files/post-images/");
    assertThat(returnedUrl).endsWith(".webp");

    String savedFileName = returnedUrl.substring(returnedUrl.lastIndexOf("/") + 1);
    Path actualPath = Paths.get(fileProperties.getPath().getLocal(), "post-images", savedFileName);

    System.out.println("실제 저장된 파일 경로: " + actualPath.toString());

    assertThat(Files.exists(actualPath)).isTrue();
  }
}
