package sw.blog.blogbackend.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sw.blog.blogbackend.tag.dto.TagResponseDto;
import sw.blog.blogbackend.tag.entity.Tag;
import sw.blog.blogbackend.tag.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

  @InjectMocks
  private TagService tagService;

  @Mock
  private TagRepository tagRepository;

  @Test
  void getAllTags_shouldReturnAllTagsAsDto() {
    Tag tag1 = Tag.builder().tagId(1L).name("JUnit").build();
    Tag tag2 = Tag.builder().tagId(2L).name("TEST").build();

    when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

    List<TagResponseDto> result = tagService.getAllTags();

    verify(tagRepository, times(1)).findAll();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("JUnit");
    assertThat(result.get(1).getName()).isEqualTo("TEST");
  }
}
