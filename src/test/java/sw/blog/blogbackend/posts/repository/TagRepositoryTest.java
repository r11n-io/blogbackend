package sw.blog.blogbackend.posts.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import sw.blog.blogbackend.post.entity.Tag;
import sw.blog.blogbackend.post.repository.TagRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TagRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @Test
  void givenTagName_whenFindByName_thenReturnTag() {
    String tagName = "Test";
    Tag newTag = new Tag(tagName);
    tagRepository.save(newTag);

    Optional<Tag> foundTag = tagRepository.findByName(tagName);

    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(tagName);
  }

  @Test
  void givenNonExistingTagName_whenFindByName_thenReturnEmpty() {
    Optional<Tag> foundTag = tagRepository.findByName("NonExistingTag");

    assertThat(foundTag).isEmpty();
  }

  @Test
  void givenSameTagName_whenSaveTwice_thenThrowsException() {
    String tagName = "TwiceTest";
    Tag tag1 = new Tag(tagName);
    Tag tag2 = new Tag(tagName);

    tagRepository.save(tag1);

    assertThrows(RuntimeException.class, () -> {
      tagRepository.saveAndFlush(tag2);
    });
  }
}
