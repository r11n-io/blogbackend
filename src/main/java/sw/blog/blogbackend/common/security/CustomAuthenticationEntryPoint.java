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

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

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
