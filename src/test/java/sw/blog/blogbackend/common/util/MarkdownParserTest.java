package sw.blog.blogbackend.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class MarkdownParserTest {

  // 단일 이미지 파싱
  @Test
  void testSingleImageParsing() {
    String content = "게시글 내용 시작. ![대체 텍스트](https://cdn.com/image1.jpg) 내용 끝.";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(1, urls.size());
    assertEquals("https://cdn.com/image1.jpg", urls.get(0));
  }

  // 다중 이미지 파싱
  @Test
  void testMultipleImagesParsing() {
    String content = "첫 번째 이미지: ![로고](http://a.com/logo.png). 두 번째: ![사진](https://b.com/photo.gif).";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(2, urls.size());
    assertTrue(urls.contains("http://a.com/logo.png"));
    assertTrue(urls.contains("https://b.com/photo.gif"));
  }

  // 이미지 URL 내부 괄호 포함 (특수상황 정규식 안전 확인)
  @Test
  void testUriWithParenthesesInQuery() {
    String encodedUrl = "https://example.com/data?id=123&p=%28param%29";
    String content = "이미지: ![test](" + encodedUrl + ").";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(1, urls.size());
    assertEquals(encodedUrl, urls.get(0));
  }

  // 이미지 없는 경우
  @Test
  void testNoImagePresent() {
    String content = "단순 텍스트 내용입니다. 링크도 없습니다.";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertTrue(urls.isEmpty());
  }

  // 빈 문자열, null
  @Test
  void testNullOrEmptyInput() {
    assertTrue(MarkdownParser.extractImageUrls(null).isEmpty());
    assertTrue(MarkdownParser.extractImageUrls("").isEmpty());
    assertTrue(MarkdownParser.extractImageUrls("      ").isEmpty());
  }
}
