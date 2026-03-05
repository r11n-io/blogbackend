package sw.blog.blogbackend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.user.entity.User;

/**
 * 사용자 리포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * 이메일로 사용자 조회
   *
   * @param email 사용자 이메일
   * @return 사용자 엔티티
   */
  Optional<User> findByEmail(String email);
}
