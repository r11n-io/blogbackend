package sw.blog.blogbackend.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 @RestController 예외 잡음
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 유효성 검사 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });

    Map<String, Object> responseBody = new HashMap<>();

    responseBody.put("status", HttpStatus.BAD_REQUEST.value());
    responseBody.put("error", "Bad Request");
    responseBody.put("message", "요청 데이터의 유효성 검사에 실패했습니다.");
    responseBody.put("details", errors);

    return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.BAD_REQUEST);
  }

  // 그 외 별도 처리되지 않는 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
    Map<String, Object> responseBody = new HashMap<>();

    responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    responseBody.put("error", "Internal Server Error");
    responseBody.put("message", "요청 처리 중 서버 오류가 발생했습니다.");
    responseBody.put("details", ex.getMessage());

    return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
