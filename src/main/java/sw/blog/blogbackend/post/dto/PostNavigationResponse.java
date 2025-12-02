package sw.blog.blogbackend.post.dto;

import lombok.Builder;
import lombok.Getter;
import sw.blog.blogbackend.post.entity.Post;

@Builder
@Getter
public class PostNavigationResponse {

  private Long postId;
  private String title;
  private Integer seriesOrder;

  public static PostNavigationResponse from(Post post) {
    return PostNavigationResponse.builder()
        .postId(post.getId())
        .title(post.getTitle())
        .seriesOrder(post.getSeriesOrder())
        .build();
  }
}
