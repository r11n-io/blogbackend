package sw.blog.blogbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sw.blog.blogbackend.entity.Post;
import sw.blog.blogbackend.service.PostService;



@RestController
@RequestMapping("/api/post")
public class PostController {
    
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // [GET] 모든 게시글 목록 조회
    // GET http://localhost:8080/api/post
    @GetMapping
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
    
    // [GET] 특정 게시글 상세 조회
    // GET http:/localhost:8080/api/post/1
    @GetMapping("/{postId}")
    public Post getPost(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    // [POST] 새 게시글 등록
    // POST http://localhost:8080/api/post
    // JSON {"title": "첫 번째 글", "content": "스프링 부트 블로그 프로젝트"}
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        // TODO: 실제 사용 시에는 보안/유효성 검사를 위한 DTO 사용 권장
        return postService.createPost(post.getTitle(), post.getContent());
    }
    
}
