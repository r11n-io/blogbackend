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

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ImageController {

  private final ImageService imageService;

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
