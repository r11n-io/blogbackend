package sw.blog.blogbackend.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {
  private String fileUrl;
}
