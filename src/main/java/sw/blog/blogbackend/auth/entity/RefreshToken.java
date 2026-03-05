package sw.blog.blogbackend.auth.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리프레시 토큰 엔티티
 */
@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  /**
   * 토큰 ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 토큰 소유자 ID 외래키 (User의 기본키)
   */
  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  /**
   * 리프레시 토큰
   */
  @Column(nullable = false, unique = true, length = 500)
  private String token;

  /**
   * 토큰 만료 시간
   */
  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;
}
