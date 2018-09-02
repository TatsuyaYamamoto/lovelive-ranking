package net.sokontokoro_factory.lovelive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer(ApplicationConfig config) {
    List<String> allowedOriginList = config.getAllowedOrigins();
    int allowedOriginsSize = allowedOriginList.size();
    String[] allowedOriginArray = allowedOriginList.toArray(new String[allowedOriginsSize]);

    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins(allowedOriginArray)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
      }
    };
  }
}
