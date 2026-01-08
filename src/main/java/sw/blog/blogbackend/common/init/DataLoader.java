package sw.blog.blogbackend.common.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository;

@Order(1)
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // SecurityConfig Bean

  @Value("${ADMIN_EMAIL:}")
  private String adminEmail;

  @Value("${ADMIN_PASSWORD:}")
  private String adminPassword;

  @Override
  public void run(String... args) throws Exception {
    String email = adminEmail;
    String encodedPassword = passwordEncoder.encode(adminPassword);

    if (userRepository.findByEmail(email).isEmpty()) {
      User initUser = new User(null, email, encodedPassword,
          "사용자", "ROLE",
          null, null);

      userRepository.save(initUser);

      log.debug("테스트 계정 생성 완료 :: {}", email);
    }
  }
}
