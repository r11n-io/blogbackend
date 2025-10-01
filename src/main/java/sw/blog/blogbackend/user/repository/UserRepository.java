package sw.blog.blogbackend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // Optional: 검색 결과가 없을 경우 Optional 반환 널포인터예외 방지
  Optional<User> findByEmail(String email);
}
