package sw.blog.blogbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import sw.blog.blogbackend.file.config.FileProperties;
import sw.blog.blogbackend.file.config.SupabaseProperties;

@EnableScheduling
@SpringBootApplication
@PropertySource("classpath:config/jwt.properties")
@EnableConfigurationProperties({ FileProperties.class, SupabaseProperties.class })
public class BlogbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogbackendApplication.class, args);
	}

}
