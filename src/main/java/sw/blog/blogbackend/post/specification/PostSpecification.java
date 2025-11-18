package sw.blog.blogbackend.post.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.entity.Post;

public class PostSpecification {

  @SuppressWarnings("removal")
  public static Specification<Post> buildSpecification(
      PostSearchCondition condition) {
    // 태그 -> 카테고리 -> 키워드 순 하나만 적용
    if (StringUtils.hasText(condition.getTagName())) {
      return tagEquals(condition.getTagName());
    }

    if (StringUtils.hasText(condition.getCategory())) {
      return categoryEquals(condition.getCategory());
    }

    if (StringUtils.hasText(condition.getKeyword())) {
      return keywordContains(condition.getKeyword());
    }

    return Specification.where(null);
  }

  private static Specification<Post> tagEquals(String tagName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
        root.join(tagName).get("name"), tagName);
  }

  private static Specification<Post> categoryEquals(String category) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
        root.get("category"), category);
  }

  private static Specification<Post> keywordContains(String keyword) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.or(
        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
        criteriaBuilder.like(root.get("content"), "%" + keyword + "%"));
  }
}
