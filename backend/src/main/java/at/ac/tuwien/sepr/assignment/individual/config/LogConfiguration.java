package at.ac.tuwien.sepr.assignment.individual.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Configuration class for logging-related beans.
 */
@Configuration
public class LogConfiguration {

  /**
   * Default constructor for the LogConfiguration class.
   * No parameters are needed.
   */
  public LogConfiguration() { }

  /**
   * Registers a filter bean for logging requests.
   *
   * @return The filter registration bean.
   */
  @Bean
  public FilterRegistrationBean<OncePerRequestFilter> logFilter() {
    var reg = new FilterRegistrationBean<OncePerRequestFilter>(new LogFilter());
    reg.addUrlPatterns("/*");
    reg.setName("logFilter");
    reg.setOrder(Ordered.LOWEST_PRECEDENCE);
    return reg;
  }
}
