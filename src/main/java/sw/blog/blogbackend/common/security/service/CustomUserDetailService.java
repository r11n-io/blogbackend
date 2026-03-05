package sw.blog.blogbackend.common.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository;

/**
 * 사용자 상세 정보 서비스 클래스<br>
 *
 * - Spring Security에서 사용자 인증 시 필요한 사용자 정보를 제공하는 서비스
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * 사용자 이름(이메일)으로 사용자 상세 정보 로드
   *
   * @param email 사용자 이메일
   * @return UserDetails 사용자 상세 정보
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("이메일 또는 전화번호가 올바르지 않습니다."));

    return UserPrincipal.create(user);
  }

}