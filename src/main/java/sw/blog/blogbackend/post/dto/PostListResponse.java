package sw.blog.blogbackend.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.post.entity.Tag;

@Builder
@Getter
public class PostListResponse {

  private Long postId;
  private String title;
  private String content;
  private String category;
  private LocalDateTime createAt;
  private List<String> tags;

  public static PostListResponse from(Post post) {
    List<String> tagNames = post.getTags().stream()
        .map(Tag::getName)
        .collect(Collectors.toList());

    return PostListResponse.builder()
        .postId(post.getId())
        .title(post.getTitle())
        .content(post.getContent()) // 필요 시 여기서 적당히 내용 짤라서 나가야 함
        .category(post.getCategory())
        .createAt(post.getCreateAt())
        .tags(tagNames)
        .build();
  }
}
