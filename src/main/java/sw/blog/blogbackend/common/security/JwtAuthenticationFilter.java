package sw.blog.blogbackend.common.security;

import static sw.blog.blogbackend.common.constants.JwtTokenConstants.TOKEN_PREFIX;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sw.blog.blogbackend.common.security.service.CustomUserDetailService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailService customUserDetailService;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
      CustomUserDetailService customUserDetailService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customUserDetailService = customUserDetailService;
  }

  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
        String userEmail = jwtTokenProvider.getIdenfierFromJWT(jwt);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(userEmail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        // 요청 대한 상세 정보 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Spring Security Context 에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ex) {
      // logger.error("Security Context에서 사용자 인증을 설정할 수 없습니다.", ex);
    }

    filterChain.doFilter(request, response);
  }

  // HTTP 헤더에서 JWT 토큰 문자열 추출
  private String getJwtFromRequest(HttpServletRequest httpServletRequest) {
    String bearerToken = httpServletRequest.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }

    return null;
  }

}
