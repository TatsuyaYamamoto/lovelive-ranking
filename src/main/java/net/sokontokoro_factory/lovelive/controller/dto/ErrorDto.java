package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** JAX-RSによるErrorレスポンスを表現するJavaBean */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ErrorDto {
  @JsonProperty("target")
  private String target;

  @JsonProperty("message")
  private String message;

  public ErrorDto() {}

  public ErrorDto(String message) {
    this.message = message;
  }
}
