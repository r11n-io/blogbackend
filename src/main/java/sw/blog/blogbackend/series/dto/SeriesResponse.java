package sw.blog.blogbackend.series.dto;

import lombok.Builder;
import lombok.Getter;
import sw.blog.blogbackend.series.entity.Series;

@Builder
@Getter
public class SeriesResponse {
  private Long seriesId;
  private String title;
  private String description;

  public static SeriesResponse from(Series series) {
    return SeriesResponse.builder()
        .seriesId(series.getSeriesId())
        .title(series.getTitle())
        .description(series.getDescription())
        .build();
  }
}
