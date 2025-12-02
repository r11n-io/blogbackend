package sw.blog.blogbackend.series.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sw.blog.blogbackend.series.dto.SeriesCreateRequest;
import sw.blog.blogbackend.series.dto.SeriesDetailResponse;
import sw.blog.blogbackend.series.dto.SeriesResponse;
import sw.blog.blogbackend.series.entity.Series;
import sw.blog.blogbackend.series.service.SeriesService;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

  private final SeriesService seriesService;

  // [POST] 시리즈 등록
  @SuppressWarnings("null")
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<Map<String, Object>> createSeries(@RequestBody @Valid SeriesCreateRequest request) {
    Series createdSeries = seriesService.createSeries(request);

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("siriesId", createdSeries.getSeriesId());
    responseBody.put("message", "게시글이 성공적으로 등록되었습니다.");

    return ResponseEntity.created(URI.create("/posts")).body(responseBody);
  }

  // [GET] 시리즈 목록 조회
  @GetMapping
  public ResponseEntity<List<SeriesResponse>> getAllSeries() {
    return ResponseEntity.ok(seriesService.getAllSeries());
  }

  // [GET] 시리즈 상세 조회 + 연관 게시글 조회
  @GetMapping("/{seriesId}")
  public ResponseEntity<SeriesDetailResponse> getSeriesWithPosts(@PathVariable Long seriesId) {
    return ResponseEntity.ok(seriesService.getSeriesWithPosts(seriesId));
  }

}
