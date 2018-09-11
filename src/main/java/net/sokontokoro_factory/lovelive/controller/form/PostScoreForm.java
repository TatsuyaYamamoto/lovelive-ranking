package net.sokontokoro_factory.lovelive.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
