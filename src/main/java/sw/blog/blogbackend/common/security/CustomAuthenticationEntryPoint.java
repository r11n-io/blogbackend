package sw.blog.blogbackend.common.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 커스텀 인증 진입점 클래스<br>
 *
 * - Spring Security에서 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 핸들러<br>
 * - JSON 형식으로 에러 응답을 반환하여 클라이언트가 에러 정보를 쉽게 파싱할 수 있도록 함
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 인증 예외 처리
   *
   * @param request                 HttpServletRequest 요청 객체
   * @param response                HttpServletResponse 응답 객체
   * @param authenticationException AuthenticationException 예외 객체
   * @throws IOException 예외 발생 시
   */
  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authenticationException) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
    response.setContentType("application/json;charset-UTF-8");

    Map<String, Object> responseBody = new HashMap<>();

    responseBody.put("timestamp", new java.util.Date());
    responseBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    responseBody.put("error", "UNAUTHORIZED");
    responseBody.put("message", "인증 정보가 유효하지 않거나 누락되었습니다");
    responseBody.put("path", request.getRequestURI());

    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    response.getWriter().flush();
  }
}
