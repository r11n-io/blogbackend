package sw.blog.blogbackend.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.tag.entity.Tag;

@Builder
@Getter
public class PostDetailResponse {

  private Long postId;
  private String title;
  private String content;
  private String category;
  private LocalDateTime createAt;
  private List<String> tags;
  private Long seriesId;
  private String seriesTitle;
  private Integer seriesOrder;

  public static PostDetailResponse from(Post post) {
    List<String> tagNames = post.getTags().stream()
        .map(Tag::getName)
        .collect(Collectors.toList());
    Series series = post.getSeries();

    return PostDetailResponse.builder()
        .postId(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .category(post.getCategory())
        .createAt(post.getCreateAt())
        .tags(tagNames)
        .seriesId(series != null ? series.getSeriesId() : null)
        .seriesTitle(series != null ? series.getTitle() : null)
        .seriesOrder(post.getSeriesOrder())
        .build();
  }
}
