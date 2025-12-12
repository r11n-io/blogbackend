package sw.blog.blogbackend.file.dto;

import lombok.Builder;

// Lombok 등의 라이브러리도 맞춰서 업데이트되서 활용가능
@Builder
public record ImageResponse(
    String url,
    String originalFileName) {
  // record 데이터 보관 위한 불변 클래스: 내부 로깅 메소드 등 여기 선언가능
}
