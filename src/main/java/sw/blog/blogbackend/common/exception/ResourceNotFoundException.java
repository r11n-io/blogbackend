package sw.blog.blogbackend.common.exception;

/**
 * 리소스 없음 예외 클래스<br>
 *
 * - 요청한 리소스가 존재하지 않을 때 발생하는 예외
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String resourceName, Object identifier) {
    super(String.format("%s를 찾을 수 없습니다: %s", resourceName, identifier.toString()));
  }

}
