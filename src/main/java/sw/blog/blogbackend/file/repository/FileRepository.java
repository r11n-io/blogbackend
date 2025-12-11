package sw.blog.blogbackend.file.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.file.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

  List<File> findByUrlIn(List<String> urls);

  List<File> findByPostId(Long postId);

  List<File> findByIsUsedFalseAndUploadAtBefore(LocalDateTime threshold);
}
