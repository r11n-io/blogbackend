package sw.blog.blogbackend.post.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostDetailResponse;
import sw.blog.blogbackend.post.dto.PostListResponse;
import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.dto.PostUpdateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.service.PostService;

/**
 * 게시글(Post) 컨트롤러.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  /**
   * 게시글 생성
   *
   * @param request 게시글 생성 요청 DTO
   * @return 생성된 게시글 ID
   */
  @SuppressWarnings("null")
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<Map<String, Object>> createPost(
      @Valid @RequestBody PostCreateRequest request) {
    Post createdPost = postService.createPost(request);

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("postId", createdPost.getId());
    responseBody.put("message", "게시글이 성공적으로 등록되었습니다.");

    // 게시글 상세 조회 URI Location 헤더에 포함 (RESTful 권장)
    return ResponseEntity
        .created(URI.create("/api/posts/" + createdPost.getId()))
        .body(responseBody);
  }

  /**
   * 게시글 목록 조회
   *
   * @param condition 게시글 검색 조건 DTO
   * @param page      페이지 번호 (0부터 시작)
   * @param size      페이지당 게시글 수
   * @return 게시글 DTO 목록
   */
  @GetMapping
  public ResponseEntity<List<PostListResponse>> getPosts(
      @ModelAttribute PostSearchCondition condition,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size) {
    return ResponseEntity.ok(postService.getAllPosts(condition, page, size));
  }

  /**
   * 게시글 상세 조회
   *
   * @param postId 게시글 ID
   * @return 게시글 상세 DTO
   */
  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId) {
    return ResponseEntity.ok(postService.getPostById(postId));
  }

  /**
   * 게시글 총 건수 조회
   *
   * @param condition 게시글 검색 조건 DTO
   * @return 게시글 총 건수
   */
  @GetMapping("/count")
  public ResponseEntity<Long> getPostsCount(
      @ModelAttribute PostSearchCondition condition) {
    return ResponseEntity.ok(postService.getAllPostsCount(condition));
  }

  /**
   * 게시글 수정
   *
   * @param postId  게시글 ID
   * @param request 게시글 수정 요청 DTO
   * @return 수정된 게시글 ID와 성공 메시지
   */
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{postId}")
  public ResponseEntity<Map<String, Object>> updatePost(
      @PathVariable Long postId,
      @Valid @RequestBody PostUpdateRequest request) {

    postService.updatePost(postId, request);

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("postId", postId);
    responseBody.put("message", "게시글이 성공적으로 수정되었습니다.");

    return ResponseEntity.ok(responseBody);
  }

  /**
   * 게시글 삭제
   *
   * @param postId 게시글 ID
   * @return 204 No Content
   */
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    postService.deletePost(postId);

    return ResponseEntity.noContent().build();
  }
}
