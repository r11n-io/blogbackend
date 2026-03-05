package sw.blog.blogbackend.series.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 시리즈 엔티티
 */
@Entity
@Table(name = "series")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Series {

  /*
   * ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seriesId;

  /*
   * 제목
   */
  private String title;

  /*
   * 설명
   */
  @Column(columnDefinition = "TEXT")
  private String description;
}
