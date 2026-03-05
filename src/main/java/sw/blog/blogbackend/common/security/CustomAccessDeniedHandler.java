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

/**
 * 커스텀 접근 거부 핸들러 클래스<br>
 *
 * - Spring Security에서 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출되는 핸들러<br>
 * - JSON 형식으로 에러 응답을 반환하여 클라이언트가 에러 정보를 쉽게 파싱할 수 있도록 함
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 접근 거부 예외 처리
   *
   * @param request               HttpServletRequest 요청 객체
   * @param response              HttpServletResponse 응답 객체
   * @param accessDeniedException AccessDeniedException 예외 객체
   * @throws IOException 예외 발생 시
   */
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
