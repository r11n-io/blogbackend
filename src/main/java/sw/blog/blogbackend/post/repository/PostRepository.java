package sw.blog.blogbackend.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @EntityGraph(attributePaths = { "tags" })
  @NonNull
  List<Post> findAll();

  List<Post> findAllByTitleContaining(String title);
}
