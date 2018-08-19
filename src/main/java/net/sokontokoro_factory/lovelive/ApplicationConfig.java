package net.sokontokoro_factory.lovelive;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class ApplicationConfig extends SpringBootServletInitializer {
  @Valid private final Credential credential = new Credential();

  @Getter
  @Setter
  public static class Credential {
    @NotBlank private String twitterKey;
    @NotBlank private String twitterSecret;
  }
}
