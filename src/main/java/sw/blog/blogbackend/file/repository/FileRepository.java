package sw.blog.blogbackend.file.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.file.entity.File;

/**
 * 파일 리포지토리
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {

  /**
   * URL 목록으로 파일 조회
   * 
   * @param urls URL 리스트
   * @return 파일 엔티티 리스트
   */
  List<File> findByUrlIn(List<String> urls);

  /**
   * 게시글 ID로 파일 조회
   * 
   * @param postId 게시글 ID
   * @return 파일 엔티티 리스트
   */
  List<File> findByPostId(Long postId);

  /**
   * 사용되지 않은 파일 중 업로드 시간이 특정 시간 이전인 파일 조회
   * 
   * @param threshold 업로드 시간 임계값
   * @return 파일 엔티티 리스트
   */
  List<File> findByIsUsedFalseAndUploadAtBefore(LocalDateTime threshold);
}
