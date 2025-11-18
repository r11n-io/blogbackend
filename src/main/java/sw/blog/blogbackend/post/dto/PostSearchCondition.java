package sw.blog.blogbackend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostSearchCondition {

  private String tagName;
  private String category;
  private String keyword;
}
