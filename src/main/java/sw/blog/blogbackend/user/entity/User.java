package sw.blog.blogbackend.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
 * 사용자 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

  /*
   * 사용자 ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /*
   * 이메일
   */
  @Column(nullable = false, unique = true)
  private String email;

  /*
   * 비밀번호
   */
  @Column(nullable = false)
  private String password;

  /*
   * 사용자 이름
   */
  @Column(nullable = false, length = 50)
  private String userName;

  /*
   * 역할
   */
  @Column(nullable = false)
  private String role;

  /*
   * 생성일
   */
  @CreatedDate
  private LocalDateTime createAt;

  /*
   * 수정일
   */
  @LastModifiedDate
  private LocalDateTime updateAt;

}
