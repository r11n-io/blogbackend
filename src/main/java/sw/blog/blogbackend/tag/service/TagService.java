package sw.blog.blogbackend.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.tag.dto.TagResponse;
import sw.blog.blogbackend.tag.repository.TagRepository;

/**
 * 태그 서비스
 */
@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  /**
   * 전체 태그 조회
   * 
   * @return 전체 태그 목록
   */
  public List<TagResponse> getAllTags() {
    return tagRepository.findAll().stream()
        .map(tag -> TagResponse.builder()
            .name(tag.getName())
            .build())
        .collect(Collectors.toList());
  }
}
