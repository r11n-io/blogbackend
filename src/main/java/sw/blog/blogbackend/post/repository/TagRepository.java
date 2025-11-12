package sw.blog.blogbackend.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sw.blog.blogbackend.post.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByName(String name);
}
