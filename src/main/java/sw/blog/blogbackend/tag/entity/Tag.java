package sw.blog.blogbackend.tag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 태그 엔티티
 */
@Entity
@Table(name = "tags")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Tag {

  public Tag(String name) {
    this.name = name;
  };

  /*
   * 태그 ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private Long tagId;

  /*
   * 태그 이름
   */
  @Column(name = "name", unique = true, nullable = false, length = 50)
  private String name;

}
