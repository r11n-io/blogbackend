package sw.blog.blogbackend.tag.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponseDto {

  private String tag_id;
  private String name;
}
