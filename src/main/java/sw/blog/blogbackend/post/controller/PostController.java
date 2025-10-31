package sw.blog.blogbackend.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // [GET] 모든 게시글 목록 조회
    // GET http://localhost:8080/api/posts
    @GetMapping
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }

    // [GET] 특정 게시글 상세 조회
    // GET http:/localhost:8080/api/posts/1
    @GetMapping("/{postId}")
    public Post getPost(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    // [POST] 새 게시글 등록
    // POST http://localhost:8080/api/posts
    // JSON {"title": "첫 번째 글", "content": "스프링 부트 블로그 프로젝트"}
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostCreateRequest request) {
        Post createdPost = postService.createPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

}
