package sw.blog.blogbackend.post.service;

import java.util.HashSet;
import java.util.List;
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
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostDetailResponse;
import sw.blog.blogbackend.post.dto.PostListResponse;
import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.post.specification.PostSpecification;
import sw.blog.blogbackend.tag.entity.Tag;
import sw.blog.blogbackend.tag.repository.TagRepository;

@Service
@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final TagRepository tagRepository;

  // 1. 새 게시글 저장
  @Transactional
  @SuppressWarnings("null")
  public Post createPost(PostCreateRequest request) {
    Set<Tag> tags = getOrCreateTag(request.getTags());

    Post newPost = Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .category(request.getCategory())
        .isPrivate(request.isPrivate())
        .tags(tags)
        .build();

    return postRepository.save(newPost);
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

  // 2. 전체 게시글 목록 조회 (페이징)
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

  // 3. 특정 게시글 상세 조회
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
}
