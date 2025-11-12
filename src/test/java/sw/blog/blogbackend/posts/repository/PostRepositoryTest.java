package sw.blog.blogbackend.posts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.entity.Tag;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.post.repository.TagRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @SuppressWarnings("null")
  @Test
  void givenPostWithTag_whenSaveAndFetch_thenRelationIsIntact() {
    Tag existingTag = new Tag("Spring boot");
    tagRepository.save(existingTag);

    Tag newTag = new Tag("Spring framework");
    Set<Tag> tags = new HashSet<>(Arrays.asList(existingTag, newTag));

    Post newPost = Post.builder()
        .title("Repository Relation 테스트")
        .content("@ManyToMany 관계 레포지토리 테스트")
        .category("TEST")
        .isPrivate(false)
        .tags(tags)
        .build();

    Post savedPost = postRepository.saveAndFlush(newPost);

    // 검증(Then)
    Long postId = savedPost.getId();

    assertThat(postId).isNotNull();

    Optional<Tag> foundNewTag = tagRepository.findByName("Spring framework");
    assertThat(foundNewTag).isPresent();

    testEntityManager.clear();
    Optional<Post> foundPost = postRepository.findById(postId);
    assertThat(foundPost).isPresent();
    assertThat(foundPost.get().getTags()).hasSize(2);

    Set<String> tagNames = foundPost.get().getTags().stream()
        .map(Tag::getName)
        .collect(Collectors.toSet());

    assertThat(tagNames)
        .containsExactlyInAnyOrder("Spring boot", "Spring framework");
  }

}
