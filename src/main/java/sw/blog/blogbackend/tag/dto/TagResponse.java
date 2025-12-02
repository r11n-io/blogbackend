package sw.blog.blogbackend.tag.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {

  private String tag_id;
  private String name;
}
