package sw.blog.blogbackend.series.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sw.blog.blogbackend.post.dto.PostNavigationResponse;
import sw.blog.blogbackend.series.entity.Series;

// 게시글 상세조회 시리즈 DTO
@Getter
@Setter
@Builder
public class SeriesDetailResponse {
  private Long seriesId;
  private String title;
  private String description;
  private List<PostNavigationResponse> posts;

  public static SeriesDetailResponse from(Series series) {
    return SeriesDetailResponse.builder()
        .seriesId(series.getSeriesId())
        .title(series.getTitle())
        .description(series.getDescription())
        .build();
  }
}
