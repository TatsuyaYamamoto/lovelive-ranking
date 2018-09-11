package net.sokontokoro_factory.lovelive.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostScoreForm {
  @NotNull
  @JsonProperty("point")
  private Integer point;

  @NotBlank
  @JsonProperty("member")
  private String member;
}
