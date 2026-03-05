package sw.blog.blogbackend.tag.controllter;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.tag.dto.TagResponse;
import sw.blog.blogbackend.tag.service.TagService;

/**
 * 태그 컨트롤러
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  /**
   * 전체 태그 조회
   * 
   * @return 전체 태그 목록
   */
  @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
    return ResponseEntity.ok(tagService.getAllTags());
  }

}
