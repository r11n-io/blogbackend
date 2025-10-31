package sw.blog.blogbackend.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {

  @NotBlank(message = "제목은 필수 입력 사항입니다.")
  @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
  private String title;

  @NotBlank(message = "내용은 필수 입력 사항입니다.")
  private String content;
}
