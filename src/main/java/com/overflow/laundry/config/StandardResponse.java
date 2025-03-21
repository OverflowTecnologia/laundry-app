package com.overflow.laundry.config;

import com.overflow.laundry.constant.MessageResponseEnum;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
public class StandardResponse<T> {

  private boolean success;
  private String message;
  private LocalDateTime timestamp;
  private T data;

  public StandardResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.timestamp = LocalDateTime.now();
    this.data = data;
  }

  public static <T> ResponseEntity<StandardResponse<T>> success(
      MessageResponseEnum message, T data) {
    return ResponseEntity
        .status(message.status)
        .body(new StandardResponse<>(true, message.label, data));
  }

  public static <T> ResponseEntity<StandardResponse<T>> error(
      MessageResponseEnum message, T data) {
    return ResponseEntity
        .status(message.status)
        .body(new StandardResponse<>(false, message.label, data));
  }
}
