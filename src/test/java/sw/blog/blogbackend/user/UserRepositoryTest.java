package sw.blog.blogbackend.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository; 

@DataJpaTest
// 로컬 PostgreSQL DB 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
  
  @Autowired
  private UserRepository userRepository;
  private User testUser;

  @BeforeEach
  void setup() {
    testUser = new User(
      null, 
      "test@test.com", 
      "password", 
      "testName", 
      "TEST_ROLE", 
      null, 
      null);
    // 확장 JpaRepository 클래스에 기본 메소드가 있어서 또 선언할 필요 없음
    userRepository.save(testUser);
  }

  @Test
  void givenUserSaved_whenFindByEmail_thenReturnUser() {
    Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());

    // AssertJ 라이브러리 활용: 스프링부트 스타터에 포함
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@test.com");
    assertThat(foundUser.get().getId()).isNotNull();
  }

  @Test
  void givenNonExistingEmail_whenFindbyEmail_thenReturnTrue() {
    Optional<User> foundUser = userRepository.findByEmail("none@none.com");

    assertThat(foundUser).isEmpty();
  }
}
