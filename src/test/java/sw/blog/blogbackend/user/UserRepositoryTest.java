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
    userRepository.save(testUser);
  }

  @Test
  void findByEmail_userSaved_returnUser() {
    // given & when
    Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@test.com");
    assertThat(foundUser.get().getId()).isNotNull();
  }

  @Test
  void findByEmail_nonExistingEmail_returnEmpty() {
    // given & when
    Optional<User> foundUser = userRepository.findByEmail("none@none.com");

    // then
    assertThat(foundUser).isEmpty();
  }
}
