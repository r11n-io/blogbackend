package sw.blog.blogbackend.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.common.security.JwtTokenProvider;
import sw.blog.blogbackend.common.security.service.CustomUserDetailService;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostListResponse;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.service.PostService;

@SuppressWarnings("removal")
@WebMvcTest(controllers = PostController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PostService postService;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private CustomUserDetailService customUserDetailService;

	@SuppressWarnings("null")
	@Test
	@WithMockUser(username = "junitUser", roles = { "USER" })
	void givenValidRequest_whenCreatePost_thenReturn201Created() throws Exception {
		PostCreateRequest validRequest = PostCreateRequest.builder()
				.title("컨트롤러 테스트 제목")
				.content("컨트롤러 테스트 콘텐츠")
				.category("TEST")
				.isPrivate(false)
				.tags(Arrays.asList("Spring boot", "Web"))
				.build();
		Post returnPost = Post.builder()
				.id(1L).title("Mock")
				.content("Mock")
				.category("TEST")
				.isPrivate(false)
				.build();

		when(postService.createPost(any(PostCreateRequest.class)))
				.thenReturn(returnPost);

		String requestJson = objectMapper.writeValueAsString(validRequest);

		mockMvc.perform(
				post("/api/posts")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/posts/1"))
				.andExpect(jsonPath("$.postId").value(1L))
				.andExpect(jsonPath("$.message").exists());
	}

	@SuppressWarnings("null")
	@Test
	void giveninvalidRequest_whenCreatePost_thenReturn400BadRequest() throws Exception {
		PostCreateRequest invalidRequest = PostCreateRequest.builder()
				.title(" ")
				.content("유효성 에러 콘텐츠")
				.category("TEST")
				.tags(Arrays.asList())
				.build();

		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		mockMvc.perform(
				post("/api/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").exists());
	}

	@SuppressWarnings("null")
	@Test
	void whenGetPostsWithPaging_thenReturnPagedPosts() throws Exception {
		PostListResponse post1 = PostListResponse.from(Post.builder()
				.id(1L).title("게시글 1").content("Content1").category("개발").build());
		PostListResponse post2 = PostListResponse.from(Post.builder()
				.id(2L).title("게시글 2").content("Content 2").category("TEST").build());
		List<PostListResponse> posts = Arrays.asList(post1, post2);

		when(postService.getAllPosts(any(), eq(0), eq(8))).thenReturn(posts);

		mockMvc = MockMvcBuilders.standaloneSetup(new PostController(postService)).build();

		mockMvc.perform(get("/api/posts")
				.param("category", "TEST")
				.param("page", "0")
				.param("size", "8")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("게시글 1"));
	}

	@SuppressWarnings("null")
	@Test
	void givenValidId_whenGetPost_thenReturnPost() throws Exception {
		Long postId = 1L;
		Post mockPost = Post.builder()
				.id(postId).title("D1").content("C1").category("TEST").build();

		when(postService.getPostById(postId)).thenReturn(mockPost);

		mockMvc.perform(
				get("/api/posts/{postId}", postId)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("D1"));

		verify(postService, times(1)).getPostById(postId);
	}

	@SuppressWarnings("null")
	@Test
	void givenInvalidId_whenGetPost_thenHandle404NotFoundException() throws Exception {
		Long invalidId = 99L;

		when(postService.getPostById(invalidId))
				.thenThrow(new ResourceNotFoundException("게시글", invalidId));

		mockMvc.perform(
				get("/api/posts/{postid}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));

		verify(postService, times(1)).getPostById(invalidId);
	}

	@Test
	void whenGetPostsCount_thenReturnTotalCount() throws Exception {
		long mockCount = 25L;

		when(postService.getAllPostsCount(any())).thenReturn(mockCount);

		mockMvc.perform(get("/api/posts/count"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(25L));

		verify(postService, times(1)).getAllPostsCount(any());
	}
}
