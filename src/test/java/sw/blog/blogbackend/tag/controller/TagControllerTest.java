package sw.blog.blogbackend.tag.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import sw.blog.blogbackend.common.security.JwtTokenProvider;
import sw.blog.blogbackend.common.security.service.CustomUserDetailService;
import sw.blog.blogbackend.tag.controllter.TagController;
import sw.blog.blogbackend.tag.dto.TagResponse;
import sw.blog.blogbackend.tag.service.TagService;

@SuppressWarnings("removal")
@WebMvcTest(controllers = TagController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TagControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // @Autowired
  // private ObjectMapper objectMapper;

  @MockBean
  private TagService tagService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private CustomUserDetailService customUserDetailService;

  @SuppressWarnings("null")
  @Test
  void getAllTags_shouldReturnListOfTags() throws Exception {
    TagResponse dto1 = TagResponse.builder().name("JUnit").build();
    TagResponse dto2 = TagResponse.builder().name("TEST").build();

    when(tagService.getAllTags()).thenReturn(Arrays.asList(dto1, dto2));

    mockMvc.perform(get("/api/tags")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("JUnit"));
  }
}
