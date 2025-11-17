package sw.blog.blogbackend.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.tag.dto.TagResponseDto;
import sw.blog.blogbackend.tag.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public List<TagResponseDto> getAllTags() {
    return tagRepository.findAll().stream()
        .map(tag -> TagResponseDto.builder()
            .name(tag.getName())
            .build())
        .collect(Collectors.toList());
  }
}
