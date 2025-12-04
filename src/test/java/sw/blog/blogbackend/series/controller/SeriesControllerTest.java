package sw.blog.blogbackend.series.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.common.security.JwtTokenProvider;
import sw.blog.blogbackend.common.security.service.CustomUserDetailService;
import sw.blog.blogbackend.series.dto.SeriesCreateRequest;
import sw.blog.blogbackend.series.dto.SeriesDetailResponse;
import sw.blog.blogbackend.series.dto.SeriesResponse;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.series.service.SeriesService;

@SuppressWarnings({ "removal", "null" })
@WebMvcTest(controllers = SeriesController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class SeriesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SeriesService seriesService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private CustomUserDetailService customUserDetailService;

  @Test
  @WithMockUser(username = "junitUser", roles = { "USER" })
  void createSeries_return201() throws Exception {
    SeriesCreateRequest createRequest = SeriesCreateRequest.builder()
        .title("컨트롤러 테스트 시리즈")
        .description("시리즈 컨트롤러 테스트 내용.")
        .build();
    Series mockSeries = Series.builder()
        .seriesId(999L)
        .title(createRequest.getTitle())
        .description(createRequest.getDescription())
        .build();

    when(seriesService.createSeries(any())).thenReturn(mockSeries);

    mockMvc.perform(
        post("/api/series")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.seriesId").value(999L));

    verify(seriesService, times(1)).createSeries(any());
  }

  @Test
  void createSeries_return400() throws Exception {
    SeriesCreateRequest invalidRequest = SeriesCreateRequest.builder()
        .title(null).description("비정상입니다.")
        .build();

    mockMvc.perform(
        post("/api/series")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verify(seriesService, never()).createSeries(invalidRequest);
  }

  @Test
  void deleteSeries_return204() throws Exception {
    Long seriesId = 666L;

    // 호출 시 아무것도 안하고 정상종료
    doNothing().when(seriesService).deleteSeries(seriesId);

    mockMvc.perform(
        delete("/api/series/{seriesId}", seriesId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(seriesService, times(1)).deleteSeries(seriesId);
  }

  @Test
  void deleteSeries_return404() throws Exception {
    Long nonExistenId = 666L;

    doThrow(new ResourceNotFoundException("시리즈", nonExistenId))
        .when(seriesService).deleteSeries(nonExistenId);

    mockMvc.perform(
        delete("/api/series/{seriesId}", nonExistenId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());

  }

  @Test
  void getAllSeries_return200AndList() throws Exception {
    SeriesResponse mockSeries1 = SeriesResponse.builder()
        .seriesId(991L).title("컨트롤러 테스트 1")
        .build();
    List<SeriesResponse> mockList = Arrays.asList(mockSeries1);

    when(seriesService.getAllSeries()).thenReturn(mockList);

    mockMvc.perform(
        get("/api/series")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].title").value(mockSeries1.getTitle()));

    verify(seriesService, times(1)).getAllSeries();
  }

  @Test
  void getSeriesWithPosts_return200() throws Exception {
    Long seriesId = 999L;
    SeriesDetailResponse mockDetail = SeriesDetailResponse.builder()
        .seriesId(seriesId)
        .title("상세 컨트롤러 시리즈")
        .description("상세 컨트롤러 테스트 개요")
        .build();
    mockDetail.setPosts(List.of());

    when(seriesService.getSeriesWithPosts(seriesId)).thenReturn(mockDetail);

    mockMvc.perform(
        get("/api/series/{seriesId}", seriesId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.seriesId").value(seriesId))
        .andExpect(jsonPath("$.title").value(mockDetail.getTitle()));

    verify(seriesService, times(1)).getSeriesWithPosts(seriesId);
  }

  @Test
  void getSeriesWithPosts_return404() throws Exception {
    Long nonExistenSeriesId = 888L;

    when(seriesService.getSeriesWithPosts(nonExistenSeriesId))
        .thenThrow(new ResourceNotFoundException("시리즈", nonExistenSeriesId));

    mockMvc.perform(
        get("/api//series/{seriesId}", nonExistenSeriesId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());

    verify(seriesService, times(1)).getSeriesWithPosts(nonExistenSeriesId);
  }
}
