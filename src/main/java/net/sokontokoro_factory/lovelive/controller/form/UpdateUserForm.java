package net.sokontokoro_factory.lovelive.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserForm {
  @JsonProperty("user_name")
  private String userName;

  @JsonProperty("favorite")
  private String favorite;
}
