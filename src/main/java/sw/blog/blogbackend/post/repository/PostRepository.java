package sw.blog.blogbackend.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.tag.entity.Tag;

/**
 * 게시글(Post) 리포지토리.
 */
@Repository
public interface PostRepository
    extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  /**
   * 모든 게시글 조회
   *
   * @return 게시글 엔티티 리스트
   */
  @EntityGraph(attributePaths = { "tags" })
  @NonNull
  List<Post> findAll();

  /**
   * 시리즈 내 게시글 조회 (시리즈 순서 오름차순)
   * 
   * @param series
   * @return 게시글 엔티티 리스트
   */
  List<Post> findBySeriesOrderBySeriesOrderAsc(Series series);

  /**
   * 시리즈 내 게시글 조회
   *
   * @param series
   * @return 게시글 엔티티 리스트
   */
  List<Post> findBySeries(Series series);

  /**
   * 특정 태그가 포함된 게시글 수 조회
   *
   * @param tag 태그 엔티티
   * @return 해당 태그가 포함된 게시글 수
   */
  long countByTagsContaining(Tag tag);
}
