package sw.blog.blogbackend.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sw.blog.blogbackend.tag.entity.Tag;

/**
 * 태그 리포지토리
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

  /**
   * 이름으로 태그 조회
   *
   * @param name 태그 이름
   * @return 태그 엔티티
   */
  Optional<Tag> findByName(String name);
}
