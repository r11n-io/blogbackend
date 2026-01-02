package sw.blog.blogbackend.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

  private static final Pattern MARKDOWN_IMAGE_PATTERN = Pattern.compile(
      "!\\[[^\\[\\]]*\\]\\(((?:http[s]?://|/files/)[^\\s\\)]+)\\)");

  /**
   * 마크다운 텍스트에서 모든 이미지 URL 추출
   * 
   * @param markdownContent 마크다운 게시글 내용
   * @return 추출된 이미지 URL 목록 (없으면 빈 리스트)
   */
  public static List<String> extractImageUrls(String markdownContent) {
    if (markdownContent == null || markdownContent.isEmpty()) {
      return List.of();
    }

    List<String> urls = new ArrayList<>();
    Matcher matcher = MARKDOWN_IMAGE_PATTERN.matcher(markdownContent);

    while (matcher.find()) {
      String url = matcher.group(1);
      urls.add(url);
    }

    return urls;
  }
}
