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
import lombok.extern.slf4j.Slf4j;
import sw.blog.blogbackend.common.security.service.CustomUserDetailService;

/**
 * JWT 인증 필터 클래스<br>
 *
 * - HTTP 요청에서 JWT 토큰을 추출하여 유효성을 검사하고, 유효한 경우 Spring Security Context에 인증 정보를
 * 설정하는 역할
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailService customUserDetailService;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
      CustomUserDetailService customUserDetailService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.customUserDetailService = customUserDetailService;
  }

  /**
   * HTTP 요청에서 JWT 토큰을 추출하여 유효성을 검사하고, 유효한 경우 Spring Security Context에 인증 정보를 설정
   * 
   * @param request     HTTP 요청 객체
   * @param response    HTTP 응답 객체
   * @param filterChain 필터 체인 객체
   * @throws ServletException 예외 발생 시
   * @throws IOException      예외 발생 시
   */
  @SuppressWarnings("null")
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
        String userEmail = jwtTokenProvider.getIdentifierFromJWT(jwt);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(userEmail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        // 요청 대한 상세 정보 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Spring Security Context 에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ex) {
      log.warn("JWT validation error :: {}", ex.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청에서 JWT 토큰 추출
   *
   * @param httpServletRequest HTTP 요청 객체
   * @return String JWT 토큰 문자열, 존재하지 않으면 null 반환
   */
  private String getJwtFromRequest(HttpServletRequest httpServletRequest) {
    String bearerToken = httpServletRequest.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }

    return null;
  }

}
