package sw.blog.blogbackend.series.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeriesCreateRequest {

  @NotBlank(message = "시리즈 제목은 필수입니다.")
  private String title;

  private String description;
}
