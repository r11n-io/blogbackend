package sw.blog.blogbackend.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;

@Service
@Transactional(readOnly = true) // 읽기 전용
public class PostService {

  private final PostRepository postRepository;

  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  // 1. 새 게시글 저장
  @Transactional
  public Post createPost(PostCreateRequest request) {
    // 일단 백업
    // UserPrincipal principal = (UserPrincipal) SecurityContextHolder
    // .getContext().getAuthentication().getPrincipal();
    // Long currentUserId = principal.getId();

    Post newPost = new Post(null, request.getTitle(),
        request.getContent(), null);

    return postRepository.save(newPost);
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
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
  }
}
