package net.sokontokoro_factory.lovelive.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertScoreForm {
  @JsonProperty("point")
  private Integer point;
}
