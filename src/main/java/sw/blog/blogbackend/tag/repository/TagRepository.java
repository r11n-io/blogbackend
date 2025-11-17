package sw.blog.blogbackend.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sw.blog.blogbackend.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByName(String name);
}
