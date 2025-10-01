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

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User { 
  // 테이블명은 users 복수형, 엔티티 클래스는 단수형으로 사용하는 컨벤션 적용함
  // JPA 사용하는 경우 가장 일반적인 컨벤션이라고 함..

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 50)
  private String userName;

  @Column(nullable = false)
  private String role;

  @CreatedDate
  private LocalDateTime createAt;

  @LastModifiedDate
  private LocalDateTime updateAt;
}
