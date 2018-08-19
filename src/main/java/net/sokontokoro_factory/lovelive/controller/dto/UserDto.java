package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserDto {
  @JsonProperty("user_id")
  private Long id;

  @JsonProperty("user_name")
  private String name;

  @JsonProperty("admin")
  private Boolean admin;

  @JsonProperty("deleted")
  private Boolean deleted;

  @JsonProperty("create_date")
  private Long createDate;

  @JsonProperty("update_date")
  private Long updateDate;

  @JsonProperty("icon_url")
  private String iconURL;

  @JsonProperty("scores")
  private List<ScoreDto> scores;
}
