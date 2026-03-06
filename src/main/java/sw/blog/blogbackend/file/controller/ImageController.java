package sw.blog.blogbackend.file.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.file.dto.ImageResponse;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.service.ImageService;

/**
 * 이미지 컨트롤러 클래스.
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;

  /**
   * 이미지 업로드
   *
   * @param file 이미지 파일
   * @return 이미지 업로드 결과 DTO
   * @throws IOException 파일 저장 중 발생 예외
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/image")
  public ResponseEntity<ImageResponse> uploadImage(
      @RequestParam MultipartFile file) throws IOException {
    File savedFile = imageService.uploadPostImage(file);
    ImageResponse imageResponse = ImageResponse.builder()
        .url(savedFile.getUrl())
        .build();

    return ResponseEntity.ok(imageResponse);
  }
}
