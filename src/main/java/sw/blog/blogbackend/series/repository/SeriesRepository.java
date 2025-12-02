package sw.blog.blogbackend.series.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sw.blog.blogbackend.series.entity.Series;

public interface SeriesRepository extends JpaRepository<Series, Long> {

}
