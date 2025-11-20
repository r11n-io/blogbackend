package sw.blog.blogbackend.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostDetailResponse;
import sw.blog.blogbackend.post.dto.PostListResponse;
import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.tag.entity.Tag;
import sw.blog.blogbackend.tag.repository.TagRepository;

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

  @SuppressWarnings({ "null", "unchecked" })
  @Test
  void whenGetAllPostsWithPaging_thenReturnPagedPosts() {
    PostSearchCondition condition = PostSearchCondition.builder().build();
    Pageable pageable = Pageable.ofSize(8).withPage(0);
    Post post1 = Post.builder().id(1L).title("게시글 1").content("Content 1").build();
    Post post2 = Post.builder().id(2L).title("게시글 2").content("Content 2").build();
    List<Post> mockPosts = Arrays.asList(post1, post2);
    Page<Post> mockPage = new PageImpl<>(mockPosts);

    when(postRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

    List<PostListResponse> actualPosts = postService.getAllPosts(condition, 0, 8);

    assertThat(actualPosts).hasSize(2);
    assertThat(actualPosts.get(0).getTitle()).isEqualTo("게시글 1");
    verify(postRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @SuppressWarnings({ "unchecked", "null" })
  @Test
  void whenGetAllPostsWithCondition_thenReturnFilteredPosts() {
    PostSearchCondition condition = PostSearchCondition.builder()
        .keyword("Test").build();
    Post post1 = Post.builder().id(1L).title("Test 게시글 1").content("Content 1").build();
    Post post2 = Post.builder().id(2L).title("게시글 2").content("Test Content 2").build();
    List<Post> mockPosts = Arrays.asList(post1, post2);
    Page<Post> mockPage = new PageImpl<>(mockPosts);

    when(postRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(mockPage);

    List<PostListResponse> actualPosts = postService.getAllPosts(condition, 0, 8);

    assertThat(actualPosts).hasSize(2);
    verify(postRepository, times(1))
        .findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void givenValidId_whenGetPostById_thenReturnPost() {
    Long postId = 10L;
    Post mockPost = Post.builder()
        .id(postId).title("상세조회 게시글").content("상세조회 컨텐츠").build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

    PostDetailResponse actualPost = postService.getPostById(postId);

    assertThat(actualPost).isNotNull();
    assertThat(actualPost.getTitle()).isEqualTo("상세조회 게시글");
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  void givenInvalidId_thenGetPostById_thenThrowException() {
    Long invalidId = 99L;

    when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
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

  @SuppressWarnings("unchecked")
  @Test
  void whenGetAllPostsCount_thenReturnCorrectCount() {
    PostSearchCondition condition = PostSearchCondition.builder().build();
    long mockCount = 15L;

    when(postRepository.count(any(Specification.class))).thenReturn(mockCount);

    long actualCount = postService.getAllPostsCount(condition);

    assertThat(actualCount).isEqualTo(mockCount);
    verify(postRepository, times(1)).count(any(Specification.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void whenGetAllPostsCount_thenReturnCorrectCountWithSpecification() {
    PostSearchCondition condition = PostSearchCondition.builder()
        .keyword("Test").build();
    long mockCount = 20L;

    when(postRepository.count(any(Specification.class))).thenReturn(mockCount);

    long actualCount = postService.getAllPostsCount(condition);

    assertThat(actualCount).isEqualTo(mockCount);
    verify(postRepository, times(1)).count(any(Specification.class));
  }
}
