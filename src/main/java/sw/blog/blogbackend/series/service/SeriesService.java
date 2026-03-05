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

/**
 * 시리즈 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SeriesService {

  private final SeriesRepository seriesRepository;
  private final PostRepository postRepository;

  /**
   * 시리즈 등록
   * 
   * @param request 시리즈 등록 요청 DTO
   * @return 등록된 시리즈 엔티티
   */
  @SuppressWarnings("null")
  public Series createSeries(SeriesCreateRequest request) {
    Series series = Series.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .build();

    return seriesRepository.save(series);
  }

  /**
   * 전체 시리즈 조회
   * 
   * @return 전체 시리즈 목록 DTO 리스트
   */
  public List<SeriesResponse> getAllSeries() {
    List<Series> series = seriesRepository.findAll();

    return series.stream()
        .map(SeriesResponse::from)
        .collect(Collectors.toList());
  }

  /**
   * 시리즈 상세 조회 (게시글 포함)
   * 
   * @param seriesId 조회할 시리즈 ID
   * @return 시리즈 상세 정보 DTO (게시글 포함)
   */
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

  /**
   * 시리즈 삭제<br>
   * - 시리즈 삭제 시, 해당 시리즈에 속한 게시글 시리즈 null 처리
   * 
   * @param seriesId 삭제할 시리즈 ID
   */
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

}
