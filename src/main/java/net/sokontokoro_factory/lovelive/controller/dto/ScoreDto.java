package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.sokontokoro_factory.lovelive.domain.score.GameType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ScoreDto {

  @JsonProperty("game")
  private GameType game;

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("user_name")
  private String userName;

  @JsonProperty("point")
  private Integer point;

  @JsonProperty("high_score_date")
  private Long highScoreDate;

  @JsonProperty("count")
  private Integer count;

  @JsonProperty("ranking")
  private Long ranking;
}
