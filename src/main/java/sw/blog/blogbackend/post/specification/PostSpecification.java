package sw.blog.blogbackend.post.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import sw.blog.blogbackend.post.dto.PostSearchCondition;
import sw.blog.blogbackend.post.entity.Post;
import sw.blog.blogbackend.tag.entity.Tag;

/**
 * 게시글 검색 조건 스펙
 */
public class PostSpecification {

  /**
   * 게시글 검색 조건 빌더
   *
   * @param condition 게시글 검색 조건 DTO
   * @return 게시글 검색 조건 스펙
   */
  public static Specification<Post> buildSpecification(
      PostSearchCondition condition, boolean isLoggedIn) {
    Specification<Post> spec = isAccessiblePost(isLoggedIn);

    // 태그 -> 카테고리 -> 키워드 순 하나만 적용
    if (StringUtils.hasText(condition.getTagName())) {
      spec = spec.and(tagEquals(condition.getTagName()));
    }

    if (StringUtils.hasText(condition.getCategory())) {
      spec = spec.and(categoryEquals(condition.getCategory()));
    }

    if (StringUtils.hasText(condition.getKeyword())) {
      spec = spec.and(keywordContains(condition.getKeyword()));
    }

    return spec;
  }

  private static Specification<Post> isAccessiblePost(boolean isLoggedIn) {
    return (root, query, criteriaBuilder) -> {
      if (!isLoggedIn) {
        return criteriaBuilder.equal(root.get("isPrivate"), false);
      }

      return criteriaBuilder.conjunction();
    };
  }

  private static Specification<Post> tagEquals(String tagName) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(tagName)) {
        return criteriaBuilder.conjunction();
      }

      if (query != null) {
        query.distinct(true);
      }

      Join<Post, Tag> tagJoin = root.join("tags", JoinType.INNER);
      return criteriaBuilder.equal(tagJoin.get("name"), tagName);

    };
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
