package net.sokontokoro_factory.lovelive;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Component
@Validated
@Getter
@Setter
public class ApplicationConfig extends SpringBootServletInitializer {
  @Valid public final Credential credential = new Credential();

  @Getter
  @Setter
  public static class Credential {
    @NotBlank private String twitterKey;
    @NotBlank private String twitterSecret;
  }
}
