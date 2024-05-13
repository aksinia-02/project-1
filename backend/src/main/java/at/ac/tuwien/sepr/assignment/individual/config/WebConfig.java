package at.ac.tuwien.sepr.assignment.individual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web-related configurations.
 *
 * <p>This configuration effectively disables CORS; this is helpful during development but a bad idea in production.
 */
@Profile("!prod")
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Default constructor for the WebConfig class.
   * No parameters are needed.
   */
  public WebConfig() { }


  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("GET", "POST", "OPTIONS", "HEAD", "DELETE", "PUT", "PATCH");
  }
}
