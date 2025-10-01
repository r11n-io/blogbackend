package sw.blog.blogbackend.common.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sw.blog.blogbackend.user.entity.User;
import sw.blog.blogbackend.user.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("이메일 또는 전화번호가 올바르지 않습니다."));

    return UserPrincipal.create(user);
  }
}