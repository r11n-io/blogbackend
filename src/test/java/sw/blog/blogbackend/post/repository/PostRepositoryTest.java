package sw.blog.blogbackend.post.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.tag.entity.Tag;
import sw.blog.blogbackend.tag.repository.TagRepository;

@SuppressWarnings("null")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  void saveAndFlush_postWithTag_relationIsIntact() {
    // given
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

    // when
    Post savedPost = postRepository.saveAndFlush(newPost);

    // then
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

  @Test
  void findAll_withEntityGraph_fetchTagsEagerly() {
    // 미리 테이블에 넣어둔 데이터로 진행

    // given
    List<Post> posts = postRepository.findAll();
    Post fetchedPost = posts.stream()
        .filter(p -> p.getId().equals(999L))
        .findFirst()
        .orElseThrow();

    // then
    assertDoesNotThrow(() -> {
      assertThat(fetchedPost.getTags()).hasSize(2);
    });
  }

  // TODO: 시리즈 오름차순 조회 작성
  // TODO: 시리즈 조회 작성
  // TODO: 특정 게시물 조회 작성
}
