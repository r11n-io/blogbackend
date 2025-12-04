package sw.blog.blogbackend.series.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.series.dto.SeriesCreateRequest;
import sw.blog.blogbackend.series.dto.SeriesDetailResponse;
import sw.blog.blogbackend.series.dto.SeriesResponse;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.series.repository.SeriesRepository;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class SeriesServiceTest {

  @Mock
  private SeriesRepository seriesRepository;

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private SeriesService seriesService;

  @Test
  void createSeries_suceess() {
    String title = "JUnit5 테스트 개발기";
    String description = "백엔드 테스트를 이야기합니다.";
    Series series = Series.builder()
        .seriesId(999L).title(title).description(description)
        .build();
    when(seriesRepository.save(any(Series.class))).thenReturn(series);

    SeriesCreateRequest createRequest = SeriesCreateRequest.builder()
        .title(title).description(description)
        .build();
    Series newSeries = seriesService.createSeries(createRequest);

    verify(seriesRepository, times(1)).save(any(Series.class));
    assertThat(newSeries.getSeriesId()).isEqualTo(999L);
    assertThat(newSeries.getTitle()).isEqualTo(title);
    assertThat(newSeries.getDescription()).isEqualTo(description);
  }

  private Series createSeries(Long id, String title) {
    return Series.builder().seriesId(id).title(title).build();
  }

  private Post createPost(Long id, Series series, Integer order) {
    return Post.builder()
        .id(id).series(series).seriesOrder(null)
        .build();
  }

  @Test
  void deleteSeries_withNoPost_success() {
    Long seriesId = 666L;
    Series mockSeries = createSeries(seriesId, "단일 시리즈 테스트");

    when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(mockSeries));
    when(postRepository.findBySeries(mockSeries)).thenReturn(List.of());

    seriesService.deleteSeries(seriesId);

    verify(postRepository, never()).saveAll(anyList());
    verify(seriesRepository, times(1)).delete(mockSeries);
  }

  @Test
  void deleteSeries_withPosts_success() {
    Long seriesId = 666L;
    Series mockSeries = createSeries(seriesId, "연결 시리즈 테스트");

    Post post1 = createPost(661L, mockSeries, 1);
    Post post2 = createPost(662L, mockSeries, 2);
    List<Post> postsInSeries = Arrays.asList(post1, post2);

    when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(mockSeries));
    when(postRepository.findBySeries(mockSeries)).thenReturn(postsInSeries);

    seriesService.deleteSeries(seriesId);

    verify(postRepository, times(1)).saveAll(postsInSeries);
    verify(seriesRepository, times(1)).delete(mockSeries);
  }

  @Test
  void deleteSeries_notFound_throwsException() {
    Long nonExistenId = 666L;

    when(seriesRepository.findById(nonExistenId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      seriesService.deleteSeries(nonExistenId);
    });

    verify(seriesRepository, never()).delete(any(Series.class));
  }

  @Test
  void getAllSeries_shouldReturnDtoList() {
    Series series1 = createSeries(777L, "JUnit Test 1");
    Series series2 = createSeries(777L, "JUnit Test 2");
    List<Series> mockList = Arrays.asList(series1, series2);
    when(seriesRepository.findAll()).thenReturn(mockList);

    List<SeriesResponse> returnList = seriesService.getAllSeries();

    verify(seriesRepository, times(1)).findAll();
    assertThat(returnList).hasSize(2);
    assertThat(returnList.get(0).getTitle()).isEqualTo("JUnit Test 1");
  }

  @Test
  void getAllSeries_shouldReturnEmptyList() {
    when(seriesRepository.findAll()).thenReturn(List.of());

    List<SeriesResponse> resultList = seriesService.getAllSeries();

    verify(seriesRepository, times(1)).findAll();
    assertThat(resultList).isNotNull();
    assertThat(resultList).isEmpty();
  }

  @Test
  void getSeriesWithPosts_shouldReturnSeriesDetail() {
    Long seriesId = 777L;
    Series mockSeries = createSeries(seriesId, "JUnit Test 1");
    when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(mockSeries));
    List<Post> mockPosts = Arrays.asList(
        createPost(601L, mockSeries, 1),
        createPost(602L, mockSeries, 2),
        createPost(603L, mockSeries, 3));
    when(postRepository.findBySeriesOrderBySeriesOrderAsc(mockSeries)).thenReturn(mockPosts);

    SeriesDetailResponse result = seriesService.getSeriesWithPosts(seriesId);

    verify(seriesRepository, times(1)).findById(seriesId);
    verify(postRepository, times(1)).findBySeriesOrderBySeriesOrderAsc(mockSeries);

    assertThat(result).isNotNull();
    assertThat(result.getPosts()).hasSize(3);
  }

  @Test
  void getSeriesWithPosts_shouldThrowExceptionIfIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      seriesService.getSeriesWithPosts(null);
    });

    verify(seriesRepository, never()).findById(anyLong());
  }

  @Test
  void getSeriesWithPosts_shouldThrowExceptionIfNotFound() {
    Long notExistenSeriesId = 666L;
    when(seriesRepository.findById(notExistenSeriesId)).thenReturn((Optional.empty()));

    assertThrows(ResourceNotFoundException.class, () -> {
      seriesService.getSeriesWithPosts(notExistenSeriesId);
    });

    verify(postRepository, never()).findBySeriesOrderBySeriesOrderAsc(any());
  }
}
