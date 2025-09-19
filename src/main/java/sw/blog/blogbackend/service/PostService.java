package sw.blog.blogbackend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sw.blog.blogbackend.entity.Post;
import sw.blog.blogbackend.repository.PostRepository;

@Service
@Transactional(readOnly = true) // 읽기 전용
public class PostService {
    
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 1. 새 게시글 저장
    @Transactional
    public Post createPost(String title, String content) {
        // TODO: DTO 처리 예정
        Post newPost = new Post(null, title, content, null);
        return postRepository.save(newPost);
    }

    // 2. 전체 게시글 목록 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 3. 특정 게시글 상세 조회
    public Post getPostById(Long id) {
        // ID가 없으면 예외를 발생시키거나 null을 반환
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
    }
}
