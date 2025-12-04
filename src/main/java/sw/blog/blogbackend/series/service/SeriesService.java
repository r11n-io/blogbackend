package sw.blog.blogbackend.series.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.common.exception.ResourceNotFoundException;
import sw.blog.blogbackend.post.dto.PostNavigationResponse;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.repository.PostRepository;
import sw.blog.blogbackend.series.dto.SeriesCreateRequest;
import sw.blog.blogbackend.series.dto.SeriesDetailResponse;
import sw.blog.blogbackend.series.dto.SeriesResponse;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.series.repository.SeriesRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SeriesService {

  private final SeriesRepository seriesRepository;
  private final PostRepository postRepository;

  // 시리즈 등록
  @SuppressWarnings("null")
  public Series createSeries(SeriesCreateRequest request) {
    Series series = Series.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .build();

    return seriesRepository.save(series);
  }

  // 시리즈 삭제: 관련 게시물 시리즈 컬럼 업데이트
  @SuppressWarnings("null")
  public void deleteSeries(Long seriesId) {
    Series series = seriesRepository.findById(seriesId)
        .orElseThrow(() -> new ResourceNotFoundException("시리즈", seriesId));

    List<Post> postsInSeries = postRepository.findBySeries(series);

    if (!postsInSeries.isEmpty()) {
      for (Post post : postsInSeries) {
        post.setSeriesToNull();
      }

      postRepository.saveAll(postsInSeries);
    }

    seriesRepository.delete(series);
  }

  // 시리즈 목록 조회
  public List<SeriesResponse> getAllSeries() {
    List<Series> series = seriesRepository.findAll();

    return series.stream()
        .map(SeriesResponse::from)
        .collect(Collectors.toList());
  }

  // 시리즈 상세 조회 + 연관 게시글 조회
  public SeriesDetailResponse getSeriesWithPosts(Long seriesId) {
    if (seriesId == null) {
      throw new IllegalArgumentException("ID 파라미터가 누락되었습니다.");
    }

    // 1. 시리즈 엔티티 조회
    Series series = seriesRepository.findById(seriesId)
        .orElseThrow(() -> new ResourceNotFoundException("시리즈", seriesId));

    // 2. 시리즈 모든 게시글 순서대로 조회
    List<Post> seriesPosts = postRepository.findBySeriesOrderBySeriesOrderAsc(series);
    List<PostNavigationResponse> navigationList = seriesPosts.stream()
        .map(PostNavigationResponse::from)
        .collect(Collectors.toList());

    // 3. DTO 구성, 반환
    SeriesDetailResponse detailResponse = SeriesDetailResponse.from(series);

    detailResponse.setPosts(navigationList);

    return detailResponse;
  }
}
