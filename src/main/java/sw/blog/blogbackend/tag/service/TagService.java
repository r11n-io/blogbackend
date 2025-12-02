package sw.blog.blogbackend.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.tag.dto.TagResponse;
import sw.blog.blogbackend.tag.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public List<TagResponse> getAllTags() {
    return tagRepository.findAll().stream()
        .map(tag -> TagResponse.builder()
            .name(tag.getName())
            .build())
        .collect(Collectors.toList());
  }
}
