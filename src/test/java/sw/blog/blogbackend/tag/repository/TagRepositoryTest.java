package sw.blog.blogbackend.tag.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import sw.blog.blogbackend.tag.entity.Tag;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TagRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @Test
  void findByName_tagName_returnTag() {
    // given
    String tagName = "JUNIT_TEST";
    Tag newTag = new Tag(tagName);
    tagRepository.save(newTag);

    // when
    Optional<Tag> foundTag = tagRepository.findByName(tagName);

    // then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(tagName);
  }

  @Test
  void findByName_nonExistingTagName_returnEmpty() {
    // given
    Optional<Tag> foundTag = tagRepository.findByName("NonExistingTag");

    // then
    assertThat(foundTag).isEmpty();
  }

  @Test
  void findByName_sameTagName_throwsException() {
    // given
    String tagName = "TwiceTest";
    Tag tag1 = new Tag(tagName);
    Tag tag2 = new Tag(tagName);

    // when
    tagRepository.save(tag1);

    // then
    assertThrows(RuntimeException.class, () -> {
      tagRepository.saveAndFlush(tag2);
    });
  }
}
