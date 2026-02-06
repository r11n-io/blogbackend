package sw.blog.blogbackend.file.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;
import sw.blog.blogbackend.file.storage.StorageProvider;
import sw.blog.blogbackend.file.util.CustomByteArrayMultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

  private final FileService fileService;
  private final FileRepository fileRepository;
  private final StorageProvider storageProvider;

  private static final List<String> ALLOWD_IMAGE_MIME_TYPES = List.of(
      "image/jpeg",
      "image/png",
      "image/webp",
      "image/gif",
      "image/svg+xml",
      "image/avif");
  private static final int MAX_WIDTH = 1200;
  private static final float QUALITY_RATE = 0.8f;

  @Transactional
  public void updateFileUsage(List<String> newUrls, Long postId) {
    List<File> previousFiles = fileRepository.findByPostId(postId);
    List<File> filesToUse = newUrls.isEmpty() ? List.of() : fileRepository.findByUrlIn(newUrls);
    Set<String> newUrlSet = new HashSet<>(newUrls);

    for (File file : filesToUse) {
      file.setPostId(postId);
      file.setUsed(true);
    }

    for (File file : previousFiles) {
      if (!newUrlSet.contains(file.getUrl())) {
        file.setPostId(null);
        file.setUsed(false);
      }
    }
  }

  public File uploadPostImage(MultipartFile file) throws IOException {
    validateFile(file);

    MultipartFile processedFile = preprocessImage(file);

    return fileService.uploadAndSaveRecord(processedFile, "post-images");
  }

  // 이미지 형식 검사
  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("업로드된 파일이 없습니다.");
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWD_IMAGE_MIME_TYPES.contains(contentType)) {
      throw new UnsupportedOperationException("허용되지 않는 파일 형식입니다." + file.getContentType());
    }

    final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("파일 크기가 [" + (MAX_FILE_SIZE / 1024) + "]MB 를 초과합니다.");
    }
  }

  // 이미지 리사이징, 압축
  @SuppressWarnings("null")
  private MultipartFile preprocessImage(MultipartFile file) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    BufferedImage originalImage = ImageIO.read(file.getInputStream());
    int originWidth = originalImage.getWidth();
    int originHeight = originalImage.getHeight();

    Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(originalImage);

    if (originWidth > MAX_WIDTH) {
      builder.size(MAX_WIDTH, Integer.MAX_VALUE).keepAspectRatio(true);
    } else {
      builder.size(originWidth, originHeight);
    }

    builder.outputFormat("webp")
        .outputQuality(QUALITY_RATE)
        .toOutputStream(os);

    String originalName = file.getOriginalFilename();
    String newFileName = originalName.substring(0, originalName.lastIndexOf(".")) + ".webp";

    return CustomByteArrayMultipartFile.builder()
        .bytes(os.toByteArray())
        .name("file")
        .originalFilename(newFileName)
        .contentType("image/webp")
        .build();
  }

  @Transactional
  public int cleanupUnusedFiles(LocalDateTime threshold) {
    List<File> filesToDelete = fileRepository.findByIsUsedFalseAndUploadAtBefore(threshold);

    int deleteCount = 0;

    for (File file : filesToDelete) {
      try {
        storageProvider.delete(file.getUrl());
        fileRepository.delete(file);
        deleteCount++;
      } catch (Exception e) {
        log.error("미사용 파일 삭제 에러: [{}]", file.getUrl(), e);
      }
    }

    return deleteCount;
  }
}
