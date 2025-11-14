package sw.blog.blogbackend.post.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  // TODO: 테이블명 둘 다 복수형으로 변경..
  @JoinTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Builder.Default
  private Set<Tag> tags = new HashSet<>();

  public void addTag(Tag tag) {
    this.tags.add(tag);
  }
}
