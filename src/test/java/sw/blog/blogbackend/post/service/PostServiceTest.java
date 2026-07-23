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
import java.util.Collections;
import java.util.HashSet;
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
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;
import sw.blog.blogbackend.file.service.ImageService;
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

  @Mock
  private ImageService imageService;

  @Mock
  private FileRepository fileRepository;

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
  void createPost_newTags_newTagEntitiesAreCreated() {
    // given
    setupPostSaveMock();
    List<String> newTagNames = Arrays.asList(TAG_1, TAG_2);
    PostCreateRequest request = createMockRequest(newTagNames);

    // when
    when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());

    Post newPost = postService.createPost(request);

    // then
    assertThat(newPost.getId()).isEqualTo(1L);
    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(TAG_2);
  }

  @SuppressWarnings("null")
  @Test
  void createPost_existingTags_existingTagEntitiesAreReused() {
    // given
    setupPostSaveMock();
    List<String> newTagNames = Arrays.asList(TAG_1, TAG_2);
    PostCreateRequest request = createMockRequest(newTagNames);
    Tag tag1 = new Tag(TAG_1);
    Tag tag2 = new Tag(TAG_2);

    // when
    when(tagRepository.findByName(TAG_1)).thenReturn(Optional.of(tag1));
    when(tagRepository.findByName(TAG_2)).thenReturn(Optional.of(tag2));

    postService.createPost(request);

    // then
    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(TAG_2);
    verify(postRepository, times(1)).save(argThat(post -> {
      return post.getTags().size() == 2;
    }));
  }

  @Test
  void createPost_mixedAndDuplicateTags_correctProcessed() {
    // given
    setupPostSaveMock();
    String NEW_TAG = "Java";
    List<String> mixedTags = Arrays.asList(TAG_1, NEW_TAG, TAG_1, " ");
    PostCreateRequest request = createMockRequest(mixedTags);
    Tag existingTag = new Tag(TAG_1);

    // when
    when(tagRepository.findByName(TAG_1)).thenReturn(Optional.of(existingTag));
    when(tagRepository.findByName(NEW_TAG)).thenReturn(Optional.empty());

    postService.createPost(request);

    // then
    verify(tagRepository, times(1)).findByName(TAG_1);
    verify(tagRepository, times(1)).findByName(NEW_TAG);
    verify(tagRepository, never()).findByName(" ");
  }

  @SuppressWarnings({ "null", "unchecked" })
  @Test
  void getAllPosts_defaultCondition_pagedPosts() {
    // given
    PostSearchCondition condition = PostSearchCondition.builder().build();
    Pageable pageable = Pageable.ofSize(8).withPage(0);
    Post post1 = Post.builder().id(1L).title("게시글 1").content("Content 1").build();
    Post post2 = Post.builder().id(2L).title("게시글 2").content("Content 2").build();
    List<Post> mockPosts = Arrays.asList(post1, post2);
    Page<Post> mockPage = new PageImpl<>(mockPosts);

    // when
    when(postRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

    List<PostListResponse> actualPosts = postService.getAllPosts(condition, 0, 8, false);

    // then
    assertThat(actualPosts).hasSize(2);
    assertThat(actualPosts.get(0).getTitle()).isEqualTo("게시글 1");
    verify(postRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @SuppressWarnings({ "unchecked", "null" })
  @Test
  void getAllPosts_keywordCondition_filteredPosts() {
    // given
    PostSearchCondition condition = PostSearchCondition.builder()
        .keyword("Test").build();
    Post post1 = Post.builder().id(1L).title("Test 게시글 1").content("Content 1").build();
    Post post2 = Post.builder().id(2L).title("게시글 2").content("Test Content 2").build();
    List<Post> mockPosts = Arrays.asList(post1, post2);
    Page<Post> mockPage = new PageImpl<>(mockPosts);

    // when
    when(postRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(mockPage);

    List<PostListResponse> actualPosts = postService.getAllPosts(condition, 0, 8, false);

    // then
    assertThat(actualPosts).hasSize(2);
    verify(postRepository, times(1))
        .findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getPost_validId_post() {
    // given
    Long postId = 10L;
    Post mockPost = Post.builder()
        .id(postId).title("상세조회 게시글").content("상세조회 컨텐츠").build();

    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

    PostDetailResponse actualPost = postService.getPostById(postId);

    // then
    assertThat(actualPost).isNotNull();
    assertThat(actualPost.getTitle()).isEqualTo("상세조회 게시글");
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  void getPost_invalidId_postNotFound() {
    // given
    Long invalidId = 99L;

    // when
    when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

    // then
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
  void getAllPostsCount_defaultCondition_count() {
    // given
    PostSearchCondition condition = PostSearchCondition.builder().build();
    long mockCount = 15L;

    // when
    when(postRepository.count(any(Specification.class))).thenReturn(mockCount);

    long actualCount = postService.getAllPostsCount(condition, false);

    // then
    assertThat(actualCount).isEqualTo(mockCount);
    verify(postRepository, times(1)).count(any(Specification.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void getAllPostsCount_keywordCondition_count() {
    // given
    PostSearchCondition condition = PostSearchCondition.builder()
        .keyword("Test").build();
    long mockCount = 20L;

    // when
    when(postRepository.count(any(Specification.class))).thenReturn(mockCount);

    long actualCount = postService.getAllPostsCount(condition, false);

    // then
    assertThat(actualCount).isEqualTo(mockCount);
    verify(postRepository, times(1)).count(any(Specification.class));
  }

  // TODO: 수정 테스트 작성

  @SuppressWarnings("null")
  @Test
  void deletePost_postWithTagsAndImages_success() {
    // given
    Long postId = 1L;
    Tag tag1 = new Tag(TAG_1);
    Tag tag2 = new Tag(TAG_2);

    Post mockPost = Post.builder()
        .id(postId)
        .title("삭제할 제목")
        .tags(new HashSet<>(Arrays.asList(tag1, tag2)))
        .build();

    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

    File mockFile = File.builder().originalFileName("test.webp").isUsed(true).build();
    when(fileRepository.findByPostId(postId)).thenReturn(Collections.singletonList(mockFile));

    when(postRepository.countByTagsContaining(tag1)).thenReturn(1L);
    when(postRepository.countByTagsContaining(tag2)).thenReturn(2L);

    postService.deletePost(postId);

    // then
    assertThat(mockFile.isUsed()).isFalse();
    verify(tagRepository, times(1)).delete(tag1);
    verify(tagRepository, never()).delete(tag2);
    verify(postRepository, times(1)).delete(mockPost);
  }
}
