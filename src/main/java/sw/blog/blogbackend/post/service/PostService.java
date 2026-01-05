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

@Service
@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
public class PostService {

  private final ImageService imageService;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final SeriesRepository seriesRepository;
  private final FileRepository fileRepository;

  // 새 게시글 저장
  @Transactional
  @SuppressWarnings("null")
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

  // 게시글 수정
  @Transactional
  @SuppressWarnings("null")
  public Post updatePost(Long postId, PostUpdateRequest request) {
    // 게시글 & 태그
    if (postId == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", postId));
    Set<Tag> tags = getOrCreateTag(request.getTags());

    post.updateMetadata(request, tags);

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

  // [게시글 저장, 수정] 태그 리스트 -> 셋 변환
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

  // 전체 게시글 목록 조회 (페이징)
  public List<PostListResponse> getAllPosts(
      PostSearchCondition condition, int page, int size) {
    // 정렬, 페이징, 검색조건 설정
    var sort = Sort.by(Sort.Direction.DESC, "createAt");
    var pageable = PageRequest.of(page, size, sort);
    Specification<Post> spec = PostSpecification.buildSpecification(condition);

    // 목록 조회
    List<Post> posts = postRepository.findAll(spec, pageable).getContent();

    return posts.stream()
        .map(PostListResponse::from)
        .collect(Collectors.toList());
  }

  // 특정 게시글 상세 조회
  public PostDetailResponse getPostById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    Post post = postRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", id));

    return PostDetailResponse.from(post);
  }

  // 전체 게시글 총 건수 조회
  public long getAllPostsCount(PostSearchCondition condition) {
    Specification<Post> spec = PostSpecification.buildSpecification(condition);
    return postRepository.count(spec);
  }

  // 게시글 삭제
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
}
