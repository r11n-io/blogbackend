package sw.blog.blogbackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sw.blog.blogbackend.auth.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * 사용자 ID로 리프레시 토큰 조회
   * 
   * @param userId 사용자 ID
   * @return 리프레시 토큰 엔티티
   */
  Optional<RefreshToken> findByUserId(Long userId);

  /**
   * 토큰 문자열로 리프레시 토큰 조회
   * 
   * @param token 리프레시 토큰 문자열
   * @return 리프레시 토큰 엔티티
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * 리프레시 토큰 삭제
   * 
   * @param userId 사용자 ID
   */
  // TODO: Query 어노테이션 대체. 가급적 사용하지 말자
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId")
  void deleteByUserId(@Param("userId") Long userId);

}
