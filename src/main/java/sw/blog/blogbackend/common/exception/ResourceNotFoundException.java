package sw.blog.blogbackend.common.exception;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String resourceName, Object identifier) {
    super(String.format("%s를 찾을 수 없습니다: %s", resourceName, identifier.toString()));
  }

}
