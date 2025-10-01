package sw.blog.blogbackend.common.security.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sw.blog.blogbackend.user.entity.User;

public class UserPrincipal implements UserDetails {
  private Long id;
  private String email;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  // private 생성자: 팩토리 메서드만 사용하도록
  private UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  // DB User 엔티티 -> Spring Security UserPrincipal 변환
  public static UserPrincipal create(User user) {
    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

    return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), authorities);
  }

  // 필수 오버라이드 메소드 구현
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  // 이하 메소드들은 그냥 기본값 true로 쓸 꺼 같아서.. 유지

  // 이외 편의성 메소드
  public Long getId() {
    return id;
  }

  // 객체 고유 비교
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    UserPrincipal that = (UserPrincipal) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
