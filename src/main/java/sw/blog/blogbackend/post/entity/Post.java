package sw.blog.blogbackend.post.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sw.blog.blogbackend.post.dto.PostCreateRequest;
import sw.blog.blogbackend.post.dto.PostUpdateRequest;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.tag.entity.Tag;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @CreatedDate
  @Column(name = "create_at")
  @Builder.Default
  private LocalDateTime createAt = LocalDateTime.now();

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "category", nullable = false)
  private String category;

  @Column(name = "is_private", nullable = false)
  @Builder.Default
  private boolean isPrivate = false;

  // 태그 관련 컬럼 & 메소드
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "posts_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Builder.Default
  private Set<Tag> tags = new HashSet<>();

  public void addTag(Tag tag) {
    this.tags.add(tag);
  }

  // 시리즈 관련 컬럼 & 메소드
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "series_id")
  private Series series;

  private Integer seriesOrder;

  public void setSeriesToNull() {
    this.series = null;
    this.seriesOrder = null;
  }

  public void updateSeries(Series series, Integer seriesOrder) {
    this.series = series;
    this.seriesOrder = seriesOrder;
  }

  public void updateMetadata(PostUpdateRequest updateRequest, Set<Tag> tags) {
    this.title = updateRequest.getTitle();
    this.content = updateRequest.getContent();
    this.category = updateRequest.getCategory();
    this.isPrivate = updateRequest.isPrivate();
    this.tags = tags;
    // 시리즈는 서비스에서 처리
  }

  public static Post from(PostCreateRequest createRequest, Set<Tag> tags) {
    return Post.builder()
        .title(createRequest.getTitle())
        .content(createRequest.getContent())
        .category(createRequest.getCategory())
        .isPrivate(createRequest.isPrivate())
        .tags(tags)
        .build();
  }
}
