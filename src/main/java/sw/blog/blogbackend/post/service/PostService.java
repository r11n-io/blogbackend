package sw.blog.blogbackend.post.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.common.util.MarkdownParser;
import sw.blog.blogbackend.file.entity.File;
import sw.blog.blogbackend.file.repository.FileRepository;
import sw.blog.blogbackend.file.service.ImageService;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostDetailResponse;
import sw.blog.blogbackend.post.dto.PostListResponse;
import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.dto.PostUpdateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.post.specification.PostSpecification;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.series.repository.SeriesRepository;
import sw.blog.blogbackend.tag.entity.Tag;
import sw.blog.blogbackend.tag.repository.TagRepository;

/**
 * 게시글(Post) 서비스.<br>
 * - 태그, 시리즈, 이미지 관리 통합 처리
 */
@Service
@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
public class PostService {

  private final ImageService imageService;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final SeriesRepository seriesRepository;
  private final FileRepository fileRepository;

  /**
   * 게시글 생성
   *
   * @param request 게시글 생성 요청 DTO
   * @return 생성된 게시글 엔티티
   */
  @SuppressWarnings("null")
  @Transactional
  public Post createPost(PostCreateRequest request) {
    // 게시글 & 태그
    Set<Tag> tags = getOrCreateTag(request.getTags());
    Post newPost = Post.from(request, tags);

    // 시리즈 세팅
    if (request.getSeriesId() != null) {
      Series series = seriesRepository.findById(request.getSeriesId())
          .orElseThrow(() -> new ResourceNotFoundException("시리즈", request.getSeriesId()));

      newPost.setSeries(series);
      newPost.setSeriesOrder(request.getSeriesOrder());
    } else {
      newPost.setSeries(null);
      newPost.setSeriesOrder(null);
    }

    Post savedPost = postRepository.save(newPost);

    // 이미지
    List<String> imageUrls = MarkdownParser.extractImageUrls(request.getContent());
    imageService.updateFileUsage(imageUrls, savedPost.getId());

    return savedPost;
  }

  /**
   * 전체 게시글 목록 조회 (페이징)
   * 
   * @param condition 검색 조건 DTO
   * @param page      페이지 번호 (0부터 시작)
   * @param size      페이지당 게시글 수
   * @return 게시글 DTO 목록
   */
  public List<PostListResponse> getAllPosts(
      PostSearchCondition condition, int page, int size) {
    // 정렬, 페이징, 검색조건 설정
    var sort = Sort.by(Sort.Direction.DESC, "createAt");
    var pageable = PageRequest.of(page, size, sort);
    Specification<Post> spec = PostSpecification.buildSpecification(condition);

    List<Post> posts = postRepository.findAll(spec, pageable).getContent();

    return posts.stream()
        .map(PostListResponse::from)
        .collect(Collectors.toList());
  }

  /**
   * 특정 게시글 상세 조회
   *
   * @param id 게시글 ID
   * @return 게시글 상세 DTO
   * @exception IllegalArgumentException  ID 파라미터가 null일 때
   * @exception ResourceNotFoundException 해당 ID의 게시글이 존재하지 않을 때
   */
  public PostDetailResponse getPostById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    Post post = postRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", id));

    return PostDetailResponse.from(post);
  }

  /**
   * 전체 게시글 총 건수 조회
   *
   * @param condition 게시글 검색 조건 DTO
   * @return 게시글 총 건수
   */
  public long getAllPostsCount(PostSearchCondition condition) {
    Specification<Post> spec = PostSpecification.buildSpecification(condition);

    return postRepository.count(spec);
  }

  /**
   * 게시글 수정
   *
   * @param postId  게시글 ID
   * @param request 게시글 수정 요청 DTO
   * @return 수정된 게시글 엔티티
   * @exception IllegalArgumentException  ID 파라미터가 null, 또는 요청 DTO의 필수값이 누락
   * @exception ResourceNotFoundException 해당 ID의 게시글이 존재하지 않을 때
   */
  @SuppressWarnings("null")
  @Transactional
  public Post updatePost(Long postId, PostUpdateRequest request) {
    // 게시글 & 태그
    if (postId == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", postId));
    Set<Tag> oldTags = new HashSet<>(post.getTags());
    Set<Tag> newTags = getOrCreateTag(request.getTags());

    // JPA 더티체킹으로 DB 반영됨
    post.updateMetadata(request, newTags);

    for (Tag oldTag : oldTags) {
      if (!newTags.contains(oldTag)) {
        if (postRepository.countByTagsContaining(oldTag) == 1) {
          tagRepository.delete(oldTag);
        }
      }
    }

    // 시리즈 세팅
    if (request.getSeriesId() != null) {
      Series series = seriesRepository.findById(request.getSeriesId())
          .orElseThrow(() -> new ResourceNotFoundException("시리즈", request.getSeriesId()));

      if (!Objects.equals(post.getSeries(), series)
          || !Objects.equals(post.getSeriesOrder(), request.getSeriesOrder())) {
        post.setSeries(series);
        post.setSeriesOrder(request.getSeriesOrder());
      }
    } else {
      post.setSeries(null);
      post.setSeriesOrder(null);
    }

    // 이미지
    List<String> imageUrls = MarkdownParser.extractImageUrls(request.getContent());
    imageService.updateFileUsage(imageUrls, postId);

    return post;
  }

  /**
   * 게시글 삭제
   *
   * @param id 게시글 ID
   * @exception IllegalArgumentException  ID 파라미터가 null일 때
   * @exception ResourceNotFoundException 해당 ID의 게시글이 존재하지 않을 때
   */
  @SuppressWarnings("null")
  @Transactional
  public void deletePost(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    Post post = postRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", id));

    // 이미지
    List<File> images = fileRepository.findByPostId(id);
    for (File image : images) {
      image.setPostId(null);
      image.setUsed(false);
    }

    // 시리즈
    if (post.getSeries() != null) {
      // Series series = post.getSeries();
      post.setSeries(null);
    }

    // 태그
    Set<Tag> tags = post.getTags();
    post.getTags().clear();
    for (Tag tag : tags) {
      if (postRepository.countByTagsContaining(tag) == 1) {
        tagRepository.delete(tag);
      }
    }

    postRepository.delete(post);
  }

  /**
   * 태그 리스트를 받아서 존재하지 않으면 생성하여 반환
   *
   * @param tagNames 태그 이름 리스트
   * @return 태그 엔티티 셋
   */
  private Set<Tag> getOrCreateTag(List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return new HashSet<>();
    }

    Set<String> uniqueTagNames = new HashSet<>(tagNames);
    Set<Tag> tags = uniqueTagNames.stream()
        .map(tagName -> tagName.trim())
        .filter(tagName -> !tagName.isEmpty())
        .map(tagName -> {
          Optional<Tag> existingTag = tagRepository.findByName(tagName);

          return existingTag.orElseGet(() -> {
            return new Tag(tagName);
          });
        })
        .collect(Collectors.toSet());

    return tags;
  }
}
