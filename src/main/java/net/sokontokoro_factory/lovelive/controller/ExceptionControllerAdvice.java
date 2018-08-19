package net.sokontokoro_factory.lovelive.controller;

import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {
  @ExceptionHandler(InvalidArgumentException.class)
  public ResponseEntity invalidArgumentException(InvalidArgumentException e) {
    JSONObject error = new JSONObject();
    error.put("code", "-");
    error.put("messsage", "リソースが存在しません");
    error.put("messsage_system", e.getMessage());

    JSONObject response = new JSONObject();
    response.put("error", error);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(NoResourceException.class)
  public ResponseEntity noResourceException(NoResourceException e) {
    JSONObject error = new JSONObject();
    error.put("code", "-");
    error.put("messsage", "リソースが存在しません");
    error.put("messsage_system", e.getMessage());

    JSONObject response = new JSONObject();
    response.put("error", error);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.toString());
  }
}
