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

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  // [POST] 새 게시글 등록
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

  // [GET] 모든 게시글 목록 조회
  @GetMapping
  public ResponseEntity<List<PostListResponse>> getPosts(
      @ModelAttribute PostSearchCondition condition,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size) {
    return ResponseEntity.ok(postService.getAllPosts(condition, page, size));
  }

  // [GET] 특정 게시글 상세 조회
  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId) {
    return ResponseEntity.ok(postService.getPostById(postId));
  }

  // [GET] 게시글 총 건수 조회
  @GetMapping("/count")
  public ResponseEntity<Long> getPostsCount(
      @ModelAttribute PostSearchCondition condition) {
    return ResponseEntity.ok(postService.getAllPostsCount(condition));
  }

  // [PUT] 특정 게시글 수정
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

  // [DELETE] 특정 게시글 삭제
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    postService.deletePost(postId);

    return ResponseEntity.noContent().build();
  }
}
