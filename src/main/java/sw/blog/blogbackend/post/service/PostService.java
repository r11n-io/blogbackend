package sw.blog.blogbackend.post.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.entity.Tag;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.post.repository.TagRepository;

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

  // 태그 리스트 -> 셋 변환
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

  // 2. 전체 게시글 목록 조회
  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  // 3. 특정 게시글 상세 조회
  public Post getPostById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    return postRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("게시글", id));
  }
}
