package sw.blog.blogbackend.series.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sw.blog.blogbackend.series.entity.Series;

/**
 * 시리즈 리포지토리
 */
public interface SeriesRepository extends JpaRepository<Series, Long> {

}
