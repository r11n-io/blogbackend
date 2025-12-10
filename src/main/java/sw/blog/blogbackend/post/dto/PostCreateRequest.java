package sw.blog.blogbackend.post.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  private String content;

  @NotBlank(message = "분류는 필수입니다.")
  private String category;

  private boolean isPrivate;

  private List<String> tags;

  private Long seriesId;

  private Integer seriesOrder;
}
