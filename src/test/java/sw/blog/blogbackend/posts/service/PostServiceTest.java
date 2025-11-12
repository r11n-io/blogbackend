package sw.blog.blogbackend.posts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.entity.Tag;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.post.repository.TagRepository;
import sw.blog.blogbackend.post.service.PostService;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  // Mock: 테스트에 필요한 동작만 미리 정의 이후 when().thenReturn()으로 가짜 동작 정의
  @Mock
  private PostRepository postRepository;

  @Mock
  private TagRepository tagRepository;

  // 테스트 대상 실제 객체 생성하고 @Mock 객체 미리 주입
  @InjectMocks
  private PostService postService;

  private static final String TAG_1 = "JUnit";
  private static final String TAG_2 = "Spring boot";

  @SuppressWarnings("null")
  private void setupPostSaveMock() {
    when(postRepository.save(any(Post.class)))
        .thenAnswer(invocation -> {
          Post post = invocation.getArgument(0);
          if (post.getId() == null)
            post.setId(1L);
          return post;
        });
  }

  @Test
  void giveNewTags_whenCreatePost_thenNewTagEntitiesAreCreated() {
    setupPostSaveMock();
    List<String> newTagNames = Arrays.asList(TAG_1, TAG_2);
    PostCreateRequest request = createMockRequest(newTagNames);

    when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());

    Post newPost = postService.createPost(request);

    assertThat(newPost.getId()).isEqualTo(1L);
    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(TAG_2);
  }

  @SuppressWarnings("null")
  @Test
  void givenExistingTags_whenCreatePost_thenExistingTagEntitiesAreReused() {
    setupPostSaveMock();
    List<String> newTagNames = Arrays.asList(TAG_1, TAG_2);
    PostCreateRequest request = createMockRequest(newTagNames);

    Tag tag1 = new Tag(TAG_1);
    Tag tag2 = new Tag(TAG_2);

    when(tagRepository.findByName(TAG_1)).thenReturn(Optional.of(tag1));
    when(tagRepository.findByName(TAG_2)).thenReturn(Optional.of(tag2));

    postService.createPost(request);

    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(TAG_2);
    verify(postRepository, times(1)).save(argThat(post -> {
      return post.getTags().size() == 2;
    }));
  }

  @Test
  void givenMixedAndDuplicateTags_whenCreatePost_thenCorrectProcessed() {
    setupPostSaveMock();
    String NEW_TAG = "Java";
    List<String> mixedTags = Arrays.asList(TAG_1, NEW_TAG, TAG_1, " ");
    PostCreateRequest request = createMockRequest(mixedTags);

    Tag existingTag = new Tag(TAG_1);

    when(tagRepository.findByName(TAG_1)).thenReturn(Optional.of(existingTag));
    when(tagRepository.findByName(NEW_TAG)).thenReturn(Optional.empty());

    postService.createPost(request);

    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(NEW_TAG);
    verify(tagRepository, never()).findByName(" ");
  }

  @Test
  void whenGetAllPosts_thenReturnAllPosts() {
    Post post1 = Post.builder().id(1L).title("게시글 1").content("Content 1").build();
    Post post2 = Post.builder().id(2L).title("게시글 2").content("Content 2").build();
    List<Post> mockPosts = Arrays.asList(post1, post2);

    when(postRepository.findAll()).thenReturn(mockPosts);

    List<Post> actualPosts = postService.getAllPosts();

    assertThat(actualPosts).hasSize(2);
    assertThat(actualPosts.get(0).getTitle()).isEqualTo("게시글 1");
    verify(postRepository, times(1)).findAll();
  }

  @Test
  void givenValidId_whenGetPostById_thenReturnPost() {
    Long postId = 10L;
    Post mockPost = Post.builder()
        .id(postId).title("상세조회 게시글").content("상세조회 컨텐츠").build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

    Post actualPost = postService.getPostById(postId);

    assertThat(actualPost).isNotNull();
    assertThat(actualPost.getTitle()).isEqualTo("상세조회 게시글");
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  void givenInvalidId_thenGetPostById_thenThrowException() {
    Long invalidId = 99L;

    when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      postService.getPostById(invalidId);
    });
    verify(postRepository, times(1)).findById(invalidId);
  }

  private PostCreateRequest createMockRequest(List<String> tags) {
    return PostCreateRequest.builder()
        .title("JUNIT Test")
        .content("JUNIT Content")
        .category("DEV")
        .isPrivate(false)
        .tags(tags)
        .build();
  }
}
