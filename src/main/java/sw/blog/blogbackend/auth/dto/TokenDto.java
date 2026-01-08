package sw.blog.blogbackend.auth.dto;

public record TokenDto(
    String accessToken,
    String refreshToken,
    Long userId) {

}
