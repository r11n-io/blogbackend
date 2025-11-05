package sw.blog.blogbackend.auth.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginRequest {
  private String email;
  private String password;
}
