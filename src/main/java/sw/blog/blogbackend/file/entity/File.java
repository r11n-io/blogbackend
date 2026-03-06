package sw.blog.blogbackend.file.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 엔티티 클래스.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {

  /*
   * 파일 ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fileId;

  /*
   * URL
   */
  @Column(nullable = false, length = 512)
  private String url;

  /*
   * 원본 파일명
   */
  private String originalFileName;

  /*
   * 파일 크기
   */
  private Long fileSize;

  /*
   * MIME 타입
   */
  private String mimeType;

  /*
   * 업로드 시간
   */
  @Builder.Default
  private LocalDateTime uploadAt = LocalDateTime.now();

  /*
   * 사용 여부 (게시글에 첨부된 파일인지 여부)
   */
  @Builder.Default
  private boolean isUsed = false;

  /*
   * 게시글 ID
   */
  private Long postId;

}
