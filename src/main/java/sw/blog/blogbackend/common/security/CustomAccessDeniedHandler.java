package sw.blog.blogbackend.common.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF=8");

    Map<String, Object> responseBody = new HashMap<>();

    responseBody.put("timestamp", new java.util.Date());
    responseBody.put("status", HttpServletResponse.SC_FORBIDDEN);
    responseBody.put("error", "Forbidden");
    responseBody.put("message", "해당 리소스에 접근할 권한이 없습니다");
    responseBody.put("path", request.getRequestURI());

    // JSON 응답 반환
    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    response.getWriter().flush();
  }
}
