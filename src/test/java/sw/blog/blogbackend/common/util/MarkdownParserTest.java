package sw.blog.blogbackend.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class MarkdownParserTest {

  @Test
  void extractImageUrls_singleImage_singleUrls() {
    String content = "게시글 내용 시작. ![대체 텍스트](https://cdn.com/image1.jpg) 내용 끝.";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(1, urls.size());
    assertEquals("https://cdn.com/image1.jpg", urls.get(0));
  }

  @Test
  void extractImageUrls_multipleImages_multipleUrls() {
    String content = "첫 번째 이미지: ![로고](http://a.com/logo.png). 두 번째: ![사진](https://b.com/photo.gif).";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(2, urls.size());
    assertTrue(urls.contains("http://a.com/logo.png"));
    assertTrue(urls.contains("https://b.com/photo.gif"));
  }

  @Test
  void extractImageUrls_uriWithParenthesesInQuery_singleUrls() {
    String encodedUrl = "https://example.com/data?id=123&p=%28param%29";
    String content = "이미지: ![test](" + encodedUrl + ").";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertEquals(1, urls.size());
    assertEquals(encodedUrl, urls.get(0));
  }

  @Test
  void extractImageUrls_noImage_emptyList() {
    String content = "단순 텍스트 내용입니다. 링크도 없습니다.";
    List<String> urls = MarkdownParser.extractImageUrls(content);

    assertTrue(urls.isEmpty());
  }

  @Test
  void extractImageUrls_nullOrEmptyInput_emptyList() {
    assertTrue(MarkdownParser.extractImageUrls(null).isEmpty());
    assertTrue(MarkdownParser.extractImageUrls("").isEmpty());
    assertTrue(MarkdownParser.extractImageUrls("      ").isEmpty());
  }
}
