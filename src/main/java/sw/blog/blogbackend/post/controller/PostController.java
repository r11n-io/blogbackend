package sw.blog.blogbackend.post.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostListResponse;
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
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(postService.getAllPosts(page, size));
  }

  // [GET] 특정 게시글 상세 조회
  @GetMapping("/{postId}")
  public Post getPost(@PathVariable Long postId) {
    return postService.getPostById(postId);
  }

}
