package sw.blog.blogbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@PropertySource("classpath:config/jwt.properties")
public class BlogbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogbackendApplication.class, args);
	}

}
