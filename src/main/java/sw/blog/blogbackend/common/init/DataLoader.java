package sw.blog.blogbackend.common.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // SecurityConfig Bean

  @Override
  public void run(String... args) throws Exception {
    String email = "thearch90@gmail.com";
    String encodedPassword = passwordEncoder.encode("1q2w3e4r");

    if (userRepository.findByEmail(email).isEmpty()) {
      User initUser = new User(null, email, encodedPassword,
          "사용자", "ROLE",
          null, null);

      userRepository.save(initUser);

      System.out.println("테스트 계정 생성 완료 :: " + email);
    }
  }
}
