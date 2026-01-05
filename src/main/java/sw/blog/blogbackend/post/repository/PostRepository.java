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

@Repository
public interface PostRepository
    extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  @EntityGraph(attributePaths = { "tags" })
  @NonNull
  List<Post> findAll();

  List<Post> findBySeriesOrderBySeriesOrderAsc(Series series);

  List<Post> findBySeries(Series series);

  long countByTagsContaining(Tag tag);
}
